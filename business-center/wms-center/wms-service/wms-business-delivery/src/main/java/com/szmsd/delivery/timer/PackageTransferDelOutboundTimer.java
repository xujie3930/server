package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.enums.DelOutboundOrderTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.util.LockerUtil;
import com.szmsd.inventory.api.feign.PurchaseFeignService;
import com.szmsd.inventory.domain.dto.TransportWarehousingAddDTO;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class PackageTransferDelOutboundTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundService delOutboundService;
    @SuppressWarnings({"all"})
    @Autowired
    private PurchaseFeignService purchaseFeignService;

    /**
     * 每天中午12:00，晚上19:00
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    //@Scheduled(cron = "0 0 12,19 * * ?")
    public void processing() {
        String key = applicationName + ":PackageTransferDelOutboundTimer:processing";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelOutbound> queryWrapper = Wrappers.lambdaQuery();
            // 指定查询的字段
            queryWrapper.select(DelOutbound::getSellerCode,
                    DelOutbound::getWarehouseCode,
                    DelOutbound::getOrderNo,
                    DelOutbound::getId);
            queryWrapper.eq(DelOutbound::getOrderType, DelOutboundOrderTypeEnum.PACKAGE_TRANSFER.getCode());
            queryWrapper.eq(DelOutbound::getState, DelOutboundStateEnum.DELIVERED.getCode());
            queryWrapper.eq(DelOutbound::getInStock, false);
            queryWrapper.apply("seller_code in(select seller_code from bas_seller where generate_inbound_receipt=1)");

            List<DelOutbound> delOutboundList = delOutboundService.list(queryWrapper);
            List<Long> updateInStockList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(delOutboundList)) {
                // 按客户分组
                Map<String, List<DelOutbound>> sellerCodeListMap = delOutboundList.stream().collect(Collectors.groupingBy(DelOutbound::getSellerCode));
                for (String sellerCode : sellerCodeListMap.keySet()) {
                    List<DelOutbound> sellerCodeDelOutboundList = sellerCodeListMap.get(sellerCode);
                    Map<String, List<DelOutbound>> warehouseCodeListMap = sellerCodeDelOutboundList.stream().collect(Collectors.groupingBy(DelOutbound::getWarehouseCode));
                    for (String warehouseCode : warehouseCodeListMap.keySet()) {
                        List<DelOutbound> warehouseCodeDelOutboundList = warehouseCodeListMap.get(warehouseCode);
                        List<String> idList = new ArrayList<>();
                        List<String> transferNoList = new ArrayList<>();
                        for (DelOutbound delOutbound : warehouseCodeDelOutboundList) {
                            updateInStockList.add(delOutbound.getId());
                            idList.add(delOutbound.getId() + "");
                            transferNoList.add(delOutbound.getOrderNo());
                        }
                        TransportWarehousingAddDTO transportWarehousingAddDTO = new TransportWarehousingAddDTO();
                        transportWarehousingAddDTO.setIdList(idList);
                        transportWarehousingAddDTO.setTransferNoList(transferNoList);
                        transportWarehousingAddDTO.setCustomCode(sellerCode);
                        transportWarehousingAddDTO.setWarehouseCode(warehouseCode);
                        transportWarehousingAddDTO.setWarehouseMethodCode("055005");
                        transportWarehousingAddDTO.setDeliveryWay("053003");
                        transportWarehousingAddDTO.setDeliveryWayName("预约揽收");
                        transportWarehousingAddDTO.setWarehouseCategoryCode("056001");
                        transportWarehousingAddDTO.setWarehouseCategoryName("SKU");
                        this.purchaseFeignService.transportWarehousingSubmit(transportWarehousingAddDTO);
                    }
                }
                LambdaUpdateWrapper<DelOutbound> lambdaUpdateWrapper = Wrappers.lambdaUpdate();
                lambdaUpdateWrapper.set(DelOutbound::getInStock, true);
                lambdaUpdateWrapper.in(DelOutbound::getId, updateInStockList);
                this.delOutboundService.update(null, lambdaUpdateWrapper);
            }
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
