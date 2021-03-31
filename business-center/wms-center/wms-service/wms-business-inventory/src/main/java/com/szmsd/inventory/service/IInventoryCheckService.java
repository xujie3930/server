package com.szmsd.inventory.service;

import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckDTO;
import com.szmsd.inventory.domain.dto.InventoryCheckQueryDTO;

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
     * @param inventoryCheckQueryDTO inventoryCheckQueryDTO
     * @return list
     */
    List<InventoryCheck> findList(InventoryCheckQueryDTO inventoryCheckQueryDTO);

    /**
     * 查询详情
     * @param id id
     * @return InventoryCheck
     */
    InventoryCheck details(int id);

    /**
     * 盘点单确认
     * @param inventoryCheck inventoryCheck
     * @return result
     */
    int update(InventoryCheck inventoryCheck);
}
