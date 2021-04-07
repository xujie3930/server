package com.szmsd.inventory.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.domain.BaseProduct;
import com.szmsd.common.core.language.enums.LocalLanguageEnum;
import com.szmsd.common.core.utils.bean.BeanUtils;
import com.szmsd.inventory.component.RemoteComponent;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryCheckDetails;
import com.szmsd.inventory.domain.InventoryCounting;
import com.szmsd.inventory.domain.dto.AdjustRequestDto;
import com.szmsd.inventory.domain.dto.CountingRequestDto;
import com.szmsd.inventory.mapper.IInventoryCheckOpenMapper;
import com.szmsd.inventory.mapper.InventoryCheckDetailsMapper;
import com.szmsd.inventory.mapper.InventoryMapper;
import com.szmsd.inventory.service.IInventoryCheckOpenService;
import com.szmsd.inventory.service.IInventoryRecordService;
import com.szmsd.inventory.service.IInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Slf4j
@Service
public class InventoryCheckOpenServiceImpl implements IInventoryCheckOpenService {

    @Resource
    private InventoryCheckDetailsMapper inventoryCheckDetailsMapper;

    @Resource
    private IInventoryCheckOpenMapper iInventoryCheckOpenMapper;

    @Resource
    private IInventoryRecordService inventoryRecordService;

    @Resource
    private InventoryMapper inventoryMapper;

    @Resource
    private RemoteComponent remoteComponent;

    @Resource
    private IInventoryService inventoryService;

    @Resource
    private RedissonClient redissonClient;

    @Transactional
    @Override
    public boolean adjust(AdjustRequestDto adjustRequestDto) {
        String key = adjustRequestDto.getSku().concat(adjustRequestDto.getWarehouseCode());
        RLock lock = redissonClient.getLock(key);
        boolean result = false;
        try {
            if (lock.tryLock()) {
                LambdaQueryWrapper<Inventory> query = Wrappers.lambdaQuery();
                query.eq(Inventory::getSku, adjustRequestDto.getSku()).eq(Inventory::getWarehouseCode, adjustRequestDto.getWarehouseCode());
                Inventory beforeInventory = inventoryMapper.selectOne(query);
                Inventory afterInventory = new Inventory();
                if (beforeInventory == null) {
                    beforeInventory = new Inventory().setSku(adjustRequestDto.getSku()).setWarehouseCode(adjustRequestDto.getWarehouseCode()).setTotalInventory(0).setAvailableInventory(0).setAvailableInventory(0).setTotalInbound(0);
                    BaseProduct sku = remoteComponent.getSku(adjustRequestDto.getSku());
                    afterInventory.setCusCode(sku.getSellerCode());
                }
                // after inventory
                int afterTotalInventory = beforeInventory.getAvailableInventory() + adjustRequestDto.getQty();
                int afterAvailableInventory = beforeInventory.getAvailableInventory() + adjustRequestDto.getQty();
                afterInventory.setId(beforeInventory.getId()).setSku(adjustRequestDto.getSku()).setWarehouseCode(adjustRequestDto.getWarehouseCode()).setTotalInventory(afterTotalInventory).setAvailableInventory(afterAvailableInventory).setTotalInbound(afterInventory.getTotalInbound());
                result = inventoryService.saveOrUpdate(afterInventory);

                // 记录库存日志
                inventoryRecordService.saveLogs(
                        LocalLanguageEnum.INVENTORY_RECORD_TYPE_4.getKey(), beforeInventory, afterInventory, adjustRequestDto.getOrderNo(), adjustRequestDto.getOperator(), adjustRequestDto.getOperateOn(), adjustRequestDto.getQty(),
                        adjustRequestDto.getOperator(), adjustRequestDto.getOperateOn(), adjustRequestDto.getOrderNo(), adjustRequestDto.getSku(), adjustRequestDto.getWarehouseCode(), (adjustRequestDto.getQty() + "")
                );
            }
        } catch (Exception e) {
            log.error("", e);
            throw e;
        } finally {
            if (lock.isLocked()) lock.unlock();
        }
        return result;
    }

    @Transactional
    @Override
    public int counting(CountingRequestDto countingRequestDto) {
        InventoryCounting inventoryCounting = new InventoryCounting();
        BeanUtils.copyProperties(countingRequestDto, inventoryCounting);
        LambdaUpdateWrapper<InventoryCheckDetails> update = Wrappers.lambdaUpdate();
        update.eq(InventoryCheckDetails::getOrderNo,countingRequestDto.getOrderNo());
        update.eq(InventoryCheckDetails::getSku,countingRequestDto.getSku());
        update.set(InventoryCheckDetails::getSystemQty,countingRequestDto.getSystemQty());
        update.set(InventoryCheckDetails::getCountingQty,countingRequestDto.getCountingQty());
        update.set(InventoryCheckDetails::getDiffQty,countingRequestDto.getDiffQty());
        inventoryCheckDetailsMapper.update(null,update);
        return iInventoryCheckOpenMapper.insert(inventoryCounting);
    }
}
