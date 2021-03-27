package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.InboundInventoryDTO;
import com.szmsd.inventory.domain.dto.InventoryAvailableQueryDto;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryAvailableListVO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;

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
     * 冻结库存
     *
     * @param invoiceNo     invoiceNo
     * @param warehouseCode warehouseCode
     * @param sku           sku
     * @param num           num
     * @return int
     */
    int freeze(String invoiceNo, String warehouseCode, String sku, Integer num);

    /**
     * 释放冻结库存
     *
     * @param invoiceNo     invoiceNo
     * @param warehouseCode warehouseCode
     * @param sku           sku
     * @param num           num
     * @return int
     */
    int unFreeze(String invoiceNo, String warehouseCode, String sku, Integer num);

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

}

