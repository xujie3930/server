package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;

import java.util.List;

public interface WarehouseOperationDetailsMapper extends BaseMapper<WarehouseOperationDetails> {

    List<WarehouseOperationDetails> selectWarehouseOperationDetails(Integer id);

}
