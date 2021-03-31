package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckQueryDTO;

import java.util.List;

public interface InventoryCheckMapper extends BaseMapper<InventoryCheck> {

    List<InventoryCheck> findList(InventoryCheckQueryDTO inventoryCheckQueryDTO);
}
