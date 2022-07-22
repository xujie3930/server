package com.szmsd.chargerules.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.chargerules.domain.WarehouseOperationDetails;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface WarehouseOperationDetailsMapper extends BaseMapper<WarehouseOperationDetails> {

    List<WarehouseOperationDetails> selectWarehouseOperationDetails(Integer id);

    //查询不同条件的数据
    List<WarehouseOperationDetails> selectWarehouseOperationDetailsrs(@Param("id") Integer id,@Param("computeType") Integer computeType);

}
