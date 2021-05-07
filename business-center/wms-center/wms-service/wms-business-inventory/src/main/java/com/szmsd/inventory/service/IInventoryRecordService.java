package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.InventoryRecordQueryDTO;
import com.szmsd.inventory.domain.dto.InventorySkuVolumeQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryRecordVO;
import com.szmsd.inventory.domain.vo.InventorySkuVolumeVO;

import java.util.List;

public interface IInventoryRecordService extends IService<InventoryRecord> {

    void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, String receiptNo, String operator, String operateOn, Integer quantity, String... placeholder);

    void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, String receiptNo, String operator, String operateOn, Integer quantity);

    void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, Integer quantity);

    List<InventoryRecordVO> selectList(InventoryRecordQueryDTO inventoryRecordQueryDTO);

    List<InventorySkuVolumeVO> selectSkuVolume(InventorySkuVolumeQueryDTO inventorySkuVolumeQueryDTO);
}

