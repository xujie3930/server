package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.*;
import com.szmsd.inventory.domain.vo.*;

import java.util.List;

public interface IInventoryService extends IService<Inventory> {

    void inbound(InboundInventoryDTO inboundInventoryDTO);

    List<InventorySkuVO> selectList(InventorySkuQueryDTO inventorySkuQueryDTO);

    /**
     * 根据仓库编码，SKU查询可用库存
     *
     * @param queryDto queryDto
     * @return InventoryAvailableDto
     */
    List<InventoryAvailableListVO> queryAvailableList(InventoryAvailableQueryDto queryDto);

    /**
     * 根据仓库编码，SKU查询可用库存
     *
     * @param queryDto queryDto
     * @return InventoryAvailableListVO
     */
    InventoryAvailableListVO queryOnlyAvailable(InventoryAvailableQueryDto queryDto);

    /**
     * 查询SKU信息
     *
     * @param queryDto queryDto
     * @return List<InventoryVO>
     */
    List<InventoryVO> querySku(InventoryAvailableQueryDto queryDto);

    /**
     * 查询SKU信息
     *
     * @param queryDto queryDto
     * @return InventoryVO
     */
    InventoryVO queryOnlySku(InventoryAvailableQueryDto queryDto);

    /**
     * 冻结库存
     *
     * @param invoiceNo     invoiceNo
     * @param warehouseCode warehouseCode
     * @param sku           sku
     * @param num           num
     * @param freeType      freeType
     * @param cusCode       cusCode
     * @return int
     */
    int freeze(String invoiceNo, String warehouseCode, String sku, Integer num, Integer freeType, String cusCode);

    /**
     * 释放冻结库存
     *
     * @param invoiceNo     invoiceNo
     * @param warehouseCode warehouseCode
     * @param sku           sku
     * @param num           num
     * @param freeType      freeType
     * @return int
     */
    int unFreeze(String invoiceNo, String warehouseCode, String sku, Integer num, Integer freeType);

    /**
     * 扣减库存
     *
     * @param invoiceNo     invoiceNo
     * @param warehouseCode warehouseCode
     * @param sku           sku
     * @param num           num
     * @return int
     */
    int deduction(String invoiceNo, String warehouseCode, String sku, Integer num);

    /**
     * 释放扣减库存
     *
     * @param invoiceNo     invoiceNo
     * @param warehouseCode warehouseCode
     * @param sku           sku
     * @param num           num
     * @return int
     */
    int unDeduction(String invoiceNo, String warehouseCode, String sku, Integer num);

    void adjustment(InventoryAdjustmentDTO inventoryAdjustmentDTO);

    /**
     * 获取仓库SKU
     *
     * @return list
     */
    List<Inventory> getWarehouseSku();

    /**
     * 查询sku的库龄
     *
     * @param sku
     * @return
     */
    List<SkuInventoryAgeVo> queryInventoryAgeBySku(String warehouseCode, String sku);

    List<QueryFinishListVO> queryFinishList(QueryFinishListDTO queryFinishListDTO);
}

