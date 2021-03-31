package com.szmsd.inventory.service;

import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckDTO;

import java.util.List;

public interface IInventoryCheckService {

    /**
     * 申请库存盘点
     * @param inventoryCheckDTO inventoryCheckApplyDTO
     * @return result
     */
    int add(InventoryCheckDTO inventoryCheckDTO);

    /**
     * 库存盘点列表查询
     * @param inventoryCheckDTO inventoryCheckDTO
     * @return list
     */
    List<InventoryCheck> findList(InventoryCheckDTO inventoryCheckDTO);

    /**
     * 查询详情
     * @param id id
     * @return InventoryCheck
     */
    InventoryCheck details(int id);

    int update(InventoryCheck inventoryCheck);
}
