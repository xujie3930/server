package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.ReceivingRequest;
import com.szmsd.inventory.mapper.InventoryMapper;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

    @Resource
    private IInventoryRecordService iInventoryRecordService;

    /**
     * 上架入库 - Inbound - /api/inbound/receiving #B1 接收入库上架 - 修改库存
     *
     * @param receivingRequest
     */
    @Override
    public void inbound(ReceivingRequest receivingRequest) {
        log.info("上架入库：{}", receivingRequest);
        Lock lock = new ReentrantLock(true);
        try {
            // 获取锁
            lock.lock();
            // 获取库存 仓库代码 + SKU
            String sku = receivingRequest.getSku();
            String warehouseCode = receivingRequest.getWarehouseCode();
            Integer qty = receivingRequest.getQty();

            // before inventory
            Inventory beforeInventory = baseMapper.selectOne(new QueryWrapper<Inventory>().eq("warehouse_code", warehouseCode).eq("sku", sku));
            if (beforeInventory == null) {
                beforeInventory = new Inventory().setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(0).setAvailableInventory(0).setAvailableInventory(0).setTotalInbound(0);
            }

            // after inventory
            int afterTotalInventory = beforeInventory.getTotalInventory() + qty;
            int afterAvailableInventory = beforeInventory.getAvailableInventory() + qty;
            int afterTotalInbound = beforeInventory.getTotalInbound() + qty;
            Inventory afterInventory = new Inventory().setId(beforeInventory.getId()).setSku(sku).setWarehouseCode(warehouseCode).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterTotalInbound);
            this.saveOrUpdate(afterInventory);

            // 记录库存日志
            String placeholder = receivingRequest.getOperator() + "," + receivingRequest.getOperateOn() + "," + receivingRequest.getOrderNo() + "," + receivingRequest.getQty();
            iInventoryRecordService.saveLogs(LocalLanguageEnum.INVENTORY_RECORD_TYPE_1.getKey(), beforeInventory, afterInventory, receivingRequest.getOrderNo(), receivingRequest.getOperator(), receivingRequest.getOperateOn(), qty, placeholder);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            lock.unlock();
        }

    }
}