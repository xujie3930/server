package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.inventory.domain.InventoryOccupy;
import com.szmsd.inventory.mapper.InventoryOccupyMapper;
import com.szmsd.inventory.service.InventoryOccupyService;
import org.springframework.stereotype.Service;

@Service
public class InventoryOccupyServiceImpl extends ServiceImpl<InventoryOccupyMapper, InventoryOccupy> implements InventoryOccupyService {

    @Override
    public InventoryOccupy getOccupyInfo(String cusCode, String warehouseCode, String sku, String invoiceNo) {
        LambdaQueryWrapper<InventoryOccupy> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(InventoryOccupy::getCusCode, cusCode);
        queryWrapper.eq(InventoryOccupy::getWarehouseCode, warehouseCode);
        queryWrapper.eq(InventoryOccupy::getSku, sku);
        queryWrapper.eq(InventoryOccupy::getReceiptNo, invoiceNo);
        return this.getOne(queryWrapper);
    }
}
