package com.szmsd.inventory.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.InventoryRecord;
import com.szmsd.inventory.domain.dto.InventoryRecordQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryRecordVO;

import java.util.List;

public interface IInventoryRecordService extends IService<InventoryRecord> {

    void saveLogs(String type, Inventory beforeInventory, Inventory afterInventory, String receiptNo, String operator, String operateOn, Integer quantity, String placeholder);

    List<InventoryRecordVO> selectList(InventoryRecordQueryDTO inventoryRecordQueryDTO);
}

