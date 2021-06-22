package com.szmsd.inventory.job;

import cn.hutool.core.util.RandomUtil;
import com.szmsd.bas.dto.BasSellerEmailDto;
import com.szmsd.common.core.utils.DateUtils;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.http.vo.InventoryInfo;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.component.RemoteRequest;
import com.szmsd.inventory.domain.InventoryWarning;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;
import com.szmsd.inventory.service.IInventoryService;
import com.szmsd.inventory.service.IInventoryWarningService;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InventoryJobService {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RemoteComponent remoteComponent;

    @Resource
    private RemoteRequest remoteRequest;

    @Resource
    private IInventoryService iInventoryService;

    @Resource
    private IInventoryWarningService iInventoryWarningService;

    @Resource
    private Executor inventoryTaskExecutor;

    @Scheduled(cron = "0 0 2 * * ?")
    public void inventoryWarning() {
        InventoryJobService inventoryJobService = SpringUtils.getBean("inventoryJobService");
        inventoryJobService.asyncInventoryWarning();
    }

    @Async
    public void asyncInventoryWarning() {
        log.info("OMS <-> WMS 库存对比开始");
        List<BasSellerEmailDto> customerList = remoteComponent.queryCusAll();
        if (CollectionUtils.isEmpty(customerList)) {
            log.info("未查询到相关客户信息：库存对比结束");
            return;
        }
        String batchNo = DateUtils.dateTimeNow() + RandomUtil.randomNumbers(6);
        customerList.forEach(customer -> CompletableFuture.supplyAsync(() -> inventoryWarning(customer.getSellerCode()), inventoryTaskExecutor).thenAcceptAsync(data -> {
            if (CollectionUtils.isEmpty(data)) {
                return;
            }
            List<InventoryWarning> inventoryWarning = BeanMapperUtil.mapList(data, InventoryWarning.class);
            inventoryWarning.forEach(item -> item.setCusCode(customer.getSellerCode()).setBatchNo(batchNo));
            iInventoryWarningService.createAndSendEmail(null, inventoryWarning);
        }, inventoryTaskExecutor).exceptionally(e -> {
            e.printStackTrace();
            return null;
        }));
    }

    public List<WarehouseSkuCompare> inventoryWarning(String cusCode) {
        RLock lock = redissonClient.getLock("InventoryJobService:inventoryWarning:" + cusCode);
        try {
        if (lock.tryLock()) {
            // OMS 库存
            List<InventorySkuVO> inventoryListOms = iInventoryService.selectList(new InventorySkuQueryDTO().setCusCode(cusCode));
            if (CollectionUtils.isEmpty(inventoryListOms)) {
                log.info("客户[{}]没有库存", cusCode);
                return null;
            }
            List<SkuQty> inventories = inventoryListOms.stream().map(SkuQty::new).collect(Collectors.toList());
            log.info("客户[{}], 库存[{}]", cusCode, inventories);
            Map<String, List<SkuQty>> inventoryMapOms = inventories.stream().collect(Collectors.groupingBy(SkuQty::getWarehouse));

            // WMS 库存
            List<WarehouseSkuCompare> compareList = ListUtils.synchronizedList(new ArrayList<>());
            inventoryMapOms.forEach((key, value) -> value.forEach(item -> {
                WarehouseSkuCompare compare = compare(item, (warehouseCode, sku) -> {
                    List<InventoryInfo> listing = remoteRequest.listing(warehouseCode, sku);
                    if (CollectionUtils.isEmpty(listing)) {
                        return null;
                    }
                    return listing.get(0);
                });
                if (compare == null) {
                    log.info("客户[{}], 仓库[{}], SKU[{}], 没有产生差异", cusCode, key, item.getSku());
                    return;
                }
                log.info("客户[{}], 仓库[{}], SKU[{}], 产生差异", cusCode, key, item.getSku());
                compareList.add(compare);
            }));
            return compareList;
        }
        } finally {
            lock.unlock();
        }
        return null;
    }

    public WarehouseSkuCompare compare(SkuQty skuQty, BiFunction<String, String, InventoryInfo> consumer) {
        InventoryInfo inventoryInfo = consumer.apply(skuQty.warehouse, skuQty.getSku());
        WarehouseSkuCompare warehouseSkuCompares;
        if (inventoryInfo == null) {
            warehouseSkuCompares = new WarehouseSkuCompare(skuQty, 0);
            warehouseSkuCompares.setExistQty(0);
        } else {
            if (new SkuQty(inventoryInfo).equals(skuQty)) {
                return null;
            }
            warehouseSkuCompares = new WarehouseSkuCompare(skuQty, inventoryInfo.getQty());
        }
        return warehouseSkuCompares;
    }

    @Data
    @EqualsAndHashCode(callSuper = true)
    @Accessors(chain = true)
    public static class WarehouseSkuCompare extends SkuQty {
        /** WMS库存 **/
        private Integer existQty;

        public WarehouseSkuCompare(SkuQty skuQty, Integer existQty) {
            super(skuQty.getWarehouse(), skuQty.getSku(), skuQty.getQty());
            this.existQty = existQty;
        }

    }

    @Data
    @Accessors(chain = true)
    public static class SkuQty {
        /** 仓库 **/
        private String warehouse;

        /** 总库存 **/
        private String sku;

        /** 总库存 **/
        private Integer qty;

        public SkuQty() {
        }

        public SkuQty(String warehouse, String sku, Integer qty) {
            this.warehouse = warehouse;
            this.sku = sku;
            this.qty = qty;
        }

        public SkuQty(InventorySkuVO inventorySkuVO) {
            this.sku = inventorySkuVO.getSku();
            this.warehouse = inventorySkuVO.getWarehouseCode();
            this.qty = inventorySkuVO.getTotalInventory();
        }

        public SkuQty(InventoryInfo inventoryInfo) {
            this.sku = inventoryInfo.getSku();
            this.warehouse = inventoryInfo.getWarehouseCode();
            this.qty = inventoryInfo.getQty();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SkuQty skuQty = (SkuQty) o;
            return Objects.equals(sku, skuQty.sku) &&
                    Objects.equals(warehouse, skuQty.warehouse) &&
                    Objects.equals(qty, skuQty.qty);
        }

        @Override
        public int hashCode() {
            return Objects.hash(sku, warehouse, qty);
        }
    }

}
