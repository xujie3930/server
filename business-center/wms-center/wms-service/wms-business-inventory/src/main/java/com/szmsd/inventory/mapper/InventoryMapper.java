package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.Inventory;
import com.szmsd.inventory.domain.dto.InventorySkuQueryDTO;
import com.szmsd.inventory.domain.vo.InventorySkuVO;

import java.util.List;

public interface InventoryMapper extends BaseMapper<Inventory> {

    List<InventorySkuVO> selectList(InventorySkuQueryDTO inventorySkuQueryDTO);
}
