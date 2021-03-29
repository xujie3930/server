package com.szmsd.chargerules.runnable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.Operation;
import com.szmsd.chargerules.domain.WarehouseOperation;
import com.szmsd.chargerules.dto.ChargeLogDto;
import com.szmsd.chargerules.dto.OperationDTO;
import com.szmsd.chargerules.mapper.WarehouseOperationMapper;
import com.szmsd.chargerules.service.IChargeLogService;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.delivery.api.feign.DelOutboundFeignService;
import com.szmsd.delivery.dto.DelOutboundDetailDto;
import com.szmsd.delivery.dto.DelOutboundListQueryDto;
import com.szmsd.delivery.vo.DelOutboundDetailListVO;
import com.szmsd.finance.enums.BillEnum;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;
import com.szmsd.inventory.domain.vo.SkuVolumeVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class RunnableExecute {

    @Resource
    private IPayService payService;

    @Resource
    private IOperationService operationService;

    @Resource
    private IWarehouseOperationService warehouseOperationService;

    @Resource
    private InventoryFeignService inventoryFeignService;

    @Resource
    private DelOutboundFeignService delOutboundFeignService;

    @Resource
    private WarehouseOperationMapper warehouseOperationMapper;

    @Resource
    private IChargeLogService chargeLogService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 定时任务：普通操作计价扣费；每天12点，23点执行一次
     */
//    @Scheduled(cron = "0 0 13,23 * * *")
//    @Scheduled(cron = "0/60 * * * * *")
    public void executeOperation() {
        log.info("executeOperation() start...");
        RLock lock = redissonClient.getLock("executeOperation");
        try {
            if (lock.tryLock()) {
                OperationDTO operationDTO = new OperationDTO();
                List<Operation> operations = operationService.listPage(operationDTO);
                DelOutboundListQueryDto delOutbound = new DelOutboundListQueryDto();
                for (Operation operation : operations) {
                    delOutbound.setOrderType(operation.getOperationType());
                    R<List<DelOutboundDetailListVO>> rList = delOutboundFeignService.getDelOutboundDetailsList(delOutbound);
                    if (rList.getCode() != 200 || CollectionUtils.isEmpty(rList.getData())) {
                        log.error("getDelOutboundDetailsList() failed: {} {}", rList.getMsg(), rList.getData());
                        continue;
                    }
                    for (DelOutboundDetailListVO datum : rList.getData()) {
                        String customCode = datum.getCustomCode();
                        List<DelOutboundDetailDto> details = datum.getDetails();
                        if (!CollectionUtils.isEmpty(details)) {
                            int count = details.stream().mapToInt(detail -> detail.getQty().intValue()).sum();
                            BigDecimal amount = payService.calculate(operation.getFirstPrice(), operation.getNextPrice(), count);
                            if (log.isInfoEnabled())
                                log.info("orderNo: {} orderType: {} count: {} amount: {}", datum.getOrderNo(), datum.getOrderType(), count, amount);
                            ChargeLogDto chargeLogDto = new ChargeLogDto(datum.getOrderNo(), BillEnum.PayMethod.BUSINESS_OPERATE.getPaymentName(), datum.getOrderType(), true);
                            ChargeLog exist = chargeLogService.selectLog(chargeLogDto);
                            if (Objects.isNull(exist)) {
                                log.info("该单已经扣过费, chargeLogDto: {}", chargeLogDto);
                                continue;
                            }
                            ChargeLog chargeLog = new ChargeLog(datum.getOrderNo(), datum.getOrderType());
                            R resultPay = payService.pay(customCode, amount, BillEnum.PayMethod.BUSINESS_OPERATE, chargeLog);
                            if (resultPay.getCode() != 200) {
                                log.error("executeOperation() pay failed.. msg: {},data: {}", resultPay.getMsg(), resultPay.getData());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("executeOperation() execute error: ", e);
        } finally {
            if (lock.isLocked()) lock.unlock();
        }
        log.info("executeOperation() end...");
    }

    /**
     * 定时任务：储存仓租计价扣费；每周日晚上8点执行
     */
//    @Scheduled(cron = "0 0 20 * * 7")
//    @Scheduled(cron = "0/60 * * * * *")
    public void executeWarehouse() {
        log.info("executeWarehouse() start...");
        RLock lock = redissonClient.getLock("executeOperation");
        try {
            if (lock.tryLock()) {
                // 获取当前全量库存
                R<List<InventorySkuVolumeVO>> result = inventoryFeignService.querySkuVolume(new InventorySkuVolumeQueryDTO());
                List<InventorySkuVolumeVO> data = result.getData();
                if (result.getCode() != 200 || CollectionUtils.isEmpty(data)) {
                    log.error("executeWarehouse() failed: {}", result.getMsg());
                    return;
                }
                // 获取仓库的计费规则列表
                QueryWrapper<WarehouseOperation> query = new QueryWrapper<>();
                List<WarehouseOperation> warehouseOperations = warehouseOperationMapper.selectList(query);
                data.forEach(warehouseOperation -> {
                    List<SkuVolumeVO> skuVolumes = warehouseOperation.getSkuVolumes();
                    skuVolumes.forEach(skuVolume -> {
                        String datePoor = DateUtils.getDatePoor(new Date(), DateUtils.parseDate(skuVolume.getOperateOn()));
                        int days = Integer.parseInt(datePoor.substring(0, datePoor.indexOf("天")));
                        // 根据存放天数、存放体积计算应收取的费用
                        BigDecimal amount = warehouseOperationService.charge(days, skuVolume.getVolume(), warehouseOperation.getWarehouseCode(), warehouseOperations);
                        R resultPay = payService.pay(skuVolume.getCusCode(), amount, BillEnum.PayMethod.WAREHOUSE_RENT, new ChargeLog());
                        if (resultPay.getCode() != 200) {
                            log.error("executeOperation() pay failed.. msg: {},data: {}", resultPay.getMsg(), resultPay.getData());
                        }
                    });
                });
            }
        } catch (Exception e) {
            log.error("executeWarehouse() execute error: ", e);
        } finally {
            if (lock.isLocked()) lock.unlock();
        }
        log.info("executeWarehouse() end...");
    }

}
