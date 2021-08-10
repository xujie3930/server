package com.szmsd.inventory.api.factory;

import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.*;
import feign.hystrix.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventoryFeignFallback implements FallbackFactory<InventoryFeignService> {
    @Override
    public InventoryFeignService create(Throwable throwable) {
        return new InventoryFeignService() {
            @Override
            public R inbound(InboundInventoryDTO receivingRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventorySkuVolumeVO>> querySkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventoryAvailableListVO>> queryAvailableList(InventoryAvailableQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InventoryAvailableListVO> queryOnlyAvailable(InventoryAvailableQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventoryVO>> querySku(InventoryAvailableQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InventoryVO> queryOnlySku(InventoryAvailableQueryDto queryDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> freeze(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unFreeze(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unFreezeAndFreeze(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> deduction(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unDeduction(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unDeductionAndDeduction(InventoryOperateListDto operateListDto) {
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<InventorySkuVO> page(String warehouseCode, String sku, String cusCode, Integer pageSize) {
                throw new RuntimeException(throwable);
            }

            @Override
            public R adjustment(InventoryAdjustmentDTO inventoryAdjustmentDTO) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<Inventory>> getWarehouseSku() {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<SkuInventoryAgeVo>> queryInventoryAgeBySku(String warehouseCode, String sku) {
                return R.convertResultJson(throwable);
            }
        };
    }
}
