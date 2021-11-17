package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.InventoryOccupy;

public interface InventoryOccupyService extends IService<InventoryOccupy> {

    /**
     * 查询占用记录
     *
     * @param cusCode       客户编号
     * @param warehouseCode 仓库编码
     * @param sku           SKU
     * @param invoiceNo     单据号
     * @return 占用记录
     */
    InventoryOccupy getOccupyInfo(String cusCode, String warehouseCode, String sku, String invoiceNo);
}
