package com.szmsd.chargerules.runnable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.chargerules.domain.WarehouseOperation;
import com.szmsd.chargerules.factory.OrderTypeFactory;
import com.szmsd.chargerules.mapper.WarehouseOperationMapper;
import com.szmsd.chargerules.service.IOperationService;
import com.szmsd.chargerules.service.IPayService;
import com.szmsd.chargerules.service.IWarehouseOperationService;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.finance.dto.CustPayDTO;
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
import java.util.Comparator;
import java.util.Date;
import java.util.List;

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
    private WarehouseOperationMapper warehouseOperationMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private OrderTypeFactory orderTypeFactory;

    /**
     * 定时任务：储存仓租计价扣费；每周日晚上8点执行
     */
    @Scheduled(cron = "0 0 20 * * 7")
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
//                        BigDecimal amount = warehouseOperationService.charge(days, skuVolume.getVolume(), warehouseOperation.getWarehouseCode(), warehouseOperations);
                        WarehouseOperation warehouse = warehouseOperations.stream().filter(value -> value.getWarehouseCode().equals(warehouseOperation.getWarehouseCode())
                                && days > value.getChargeDays()).max(Comparator.comparing(WarehouseOperation::getChargeDays)).orElse(null);
                        if(warehouse == null) {
                            log.error("charge() 未找到收费配置 warehouseCode: {}, days: {}",warehouseOperation.getWarehouseCode(),days);
                            return;
                        }
                        ChargeLog chargeLog = new ChargeLog(warehouseOperation.getWarehouseCode());
                        chargeLog.setCurrencyCode(warehouse.getCurrencyCode());
                        BigDecimal amount = skuVolume.getVolume().multiply(warehouse.getPrice()); //体积乘以价格
                        CustPayDTO custPayDTO = setCustPayDto(skuVolume.getCusCode(), amount,chargeLog);
                        payService.pay(custPayDTO, chargeLog);
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

    private CustPayDTO setCustPayDto(String cusCode, BigDecimal amount, ChargeLog chargeLog) {
        CustPayDTO custPayDTO = new CustPayDTO();
        custPayDTO.setCusCode(cusCode);
        custPayDTO.setPayType(BillEnum.PayType.PAYMENT);
        custPayDTO.setPayMethod(BillEnum.PayMethod.WAREHOUSE_RENT);
        custPayDTO.setCurrencyCode(chargeLog.getCurrencyCode());
        custPayDTO.setAmount(amount);
        custPayDTO.setNo(chargeLog.getOrderNo());
        return custPayDTO;
    }

}
