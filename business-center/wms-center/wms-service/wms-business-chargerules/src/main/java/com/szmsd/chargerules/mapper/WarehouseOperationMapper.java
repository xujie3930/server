package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.WarehouseOperation;
import com.szmsd.chargerules.dto.WarehouseOperationDTO;
import com.szmsd.chargerules.vo.WarehouseOperationVo;

import java.util.List;

public interface WarehouseOperationMapper extends BaseMapper<WarehouseOperation> {

    List<WarehouseOperationVo> listPage(WarehouseOperationDTO dto);
}
