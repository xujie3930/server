package com.szmsd.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.inventory.domain.InventoryWarning;
import com.szmsd.inventory.domain.dto.InventoryWarningQueryDTO;

import java.util.List;

public interface InventoryWarningMapper extends BaseMapper<InventoryWarning> {

    List<InventoryWarning> selectList(InventoryWarningQueryDTO queryDTO);

}
