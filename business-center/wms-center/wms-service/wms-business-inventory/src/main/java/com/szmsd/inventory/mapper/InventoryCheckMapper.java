package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.common.datascope.annotation.DataScope;
import com.szmsd.inventory.domain.InventoryCheck;
import com.szmsd.inventory.domain.dto.InventoryCheckQueryDTO;
import com.szmsd.inventory.domain.vo.InventoryCheckVo;

import java.util.List;

public interface InventoryCheckMapper extends BaseMapper<InventoryCheck> {

    @DataScope("a.custom_code")
    List<InventoryCheckVo> findList(InventoryCheckQueryDTO inventoryCheckQueryDTO);

    InventoryCheckVo findDetails(int id);
}
