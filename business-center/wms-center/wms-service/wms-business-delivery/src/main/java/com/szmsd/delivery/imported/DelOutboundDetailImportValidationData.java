package com.szmsd.delivery.imported;

import com.szmsd.inventory.api.service.InventoryFeignClientService;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 21:32
 */
public class DelOutboundDetailImportValidationData extends CacheContext.MapCacheContext<String, InventoryAvailableListVO> implements ImportValidationData {

    private final String sellerCode;
    private final InventoryFeignClientService inventoryFeignClientService;

    public DelOutboundDetailImportValidationData(String sellerCode, InventoryFeignClientService inventoryFeignClientService) {
        this.sellerCode = sellerCode;
        this.inventoryFeignClientService = inventoryFeignClientService;
    }

    @Override
    public boolean exists(String warehouseCode, String sku) {
        // 去查询sku是否存在
        InventoryAvailableListVO data = this.getData(warehouseCode, sku);
        return null != data && null != data.getSku();
    }

    @Override
    public boolean sub(String warehouseCode, String sku, int qty) {
        // 获取sku库存
        InventoryAvailableListVO vo = this.getData(warehouseCode, sku);
        // 获取可用数量
        Integer availableInventory = vo.getAvailableInventory();
        if (null == availableInventory
                || availableInventory < qty) {
            // sku不存在或者sku库存不足
            return false;
        }
        // 扣减可用数量
        vo.subAvailableInventory(qty);
        // 刷新缓存中的可用数量
        this.put(this.buildKey(warehouseCode, sku), vo);
        // 返回本次扣减成功
        return true;
    }

    @Override
    public InventoryAvailableListVO get(String warehouseCode, String sku) {
        return this.getData(warehouseCode, sku);
    }

    @Override
    public void resetQty(String warehouseCode, String sku, int qty) {
        // 获取sku库存
        InventoryAvailableListVO vo = this.getData(warehouseCode, sku);
        if (null == vo) {
            return;
        }
        vo.addAvailableInventory(qty);
        // 刷新缓存中的可用数量
        this.put(this.buildKey(warehouseCode, sku), vo);
    }

    private InventoryAvailableListVO getData(String warehouseCode, String sku) {
        String key = this.buildKey(warehouseCode, sku);
        if (this.containsKey(key)) {
            return this.get(key);
        }
        InventoryAvailableQueryDto queryDto = new InventoryAvailableQueryDto();
        queryDto.setCusCode(this.sellerCode);
        queryDto.setWarehouseCode(warehouseCode);
        queryDto.setEqSku(sku);
        InventoryAvailableListVO vo = this.inventoryFeignClientService.queryOnlyAvailable(queryDto);
        if (null == vo) {
            vo = new InventoryAvailableListVO();
        }
        this.put(key, vo);
        return vo;
    }

    private String buildKey(String warehouseCode, String sku) {
        return warehouseCode + "_" + sku;
    }
}
