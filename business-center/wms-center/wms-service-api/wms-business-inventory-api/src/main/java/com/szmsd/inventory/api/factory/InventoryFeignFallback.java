package com.szmsd.inventory.api.factory;

import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.web.page.TableDataInfo;
import com.szmsd.inventory.api.feign.InventoryFeignService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.*;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class InventoryFeignFallback implements FallbackFactory<InventoryFeignService> {
    @Override
    public InventoryFeignService create(Throwable throwable) {
        log.info("InventoryFeignFallback Error", throwable);
        return new InventoryFeignService() {
            @Override
            public R inbound(InboundInventoryDTO receivingRequest) {
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventorySkuVolumeVO>> querySkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO) {
                log.info("InventoryFeignFallback inventorySkuVolumeQueryDTO:{}", JSONObject.toJSONString(inventorySkuVolumeQueryDTO), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventoryAvailableListVO>> queryAvailableList(InventoryAvailableQueryDto queryDto) {
                log.info("InventoryFeignFallback queryAvailableList:{}", JSONObject.toJSONString(queryDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InventoryAvailableListVO> queryOnlyAvailable(InventoryAvailableQueryDto queryDto) {
                log.info("InventoryFeignFallback queryOnlyAvailable:{}", JSONObject.toJSONString(queryDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<InventoryVO>> querySku(InventoryAvailableQueryDto queryDto) {
                log.info("InventoryFeignFallback querySku:{}", JSONObject.toJSONString(queryDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<InventoryVO> queryOnlySku(InventoryAvailableQueryDto queryDto) {
                log.info("InventoryFeignFallback queryOnlySku:{}", JSONObject.toJSONString(queryDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> freeze(InventoryOperateListDto operateListDto) {
                log.info("InventoryFeignFallback freeze:{}", JSONObject.toJSONString(operateListDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unFreeze(InventoryOperateListDto operateListDto) {
                log.info("InventoryFeignFallback unFreeze:{}", JSONObject.toJSONString(operateListDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unFreezeAndFreeze(InventoryOperateListDto operateListDto) {
                log.info("InventoryFeignFallback unFreezeAndFreeze:{}", JSONObject.toJSONString(operateListDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> deduction(InventoryOperateListDto operateListDto) {
                log.info("InventoryFeignFallback deduction:{}", JSONObject.toJSONString(operateListDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unDeduction(InventoryOperateListDto operateListDto) {
                log.info("InventoryFeignFallback unDeduction:{}", JSONObject.toJSONString(operateListDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<Integer> unDeductionAndDeduction(InventoryOperateListDto operateListDto) {
                log.info("InventoryFeignFallback unDeductionAndDeduction:{}", JSONObject.toJSONString(operateListDto), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<InventorySkuVO> page(String warehouseCode, String sku, String cusCode, Integer pageSize) {
                log.info("InventoryFeignFallback page:{} {} {} {}", warehouseCode, sku, cusCode, pageSize, throwable);
                throw new RuntimeException(throwable);
            }

            @Override
            public R adjustment(InventoryAdjustmentDTO inventoryAdjustmentDTO) {
                log.info("InventoryFeignFallback adjustment:{}", JSONObject.toJSONString(inventoryAdjustmentDTO), throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<Inventory>> getWarehouseSku() {
                log.info("InventoryFeignFallback getWarehouseSku:", throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public R<List<SkuInventoryAgeVo>> queryInventoryAgeBySku(String warehouseCode, String sku) {
                log.info("InventoryFeignFallback queryInventoryAgeBySku:{} {}", warehouseCode, sku, throwable);
                return R.convertResultJson(throwable);
            }

            @Override
            public TableDataInfo<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO) {
                log.info("InventoryFeignFallback queryFinishList:{}", JSONObject.toJSONString(queryFinishListDTO), throwable);
                throw new RuntimeException(throwable.getMessage());
            }
        };
    }
}
