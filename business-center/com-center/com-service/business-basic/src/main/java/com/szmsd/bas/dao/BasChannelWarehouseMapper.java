package com.szmsd.bas.dao;


import com.szmsd.bas.api.domain.BasChannelWarehouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasChannelWarehouseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasChannelWarehouse record);

    int insertSelective(BasChannelWarehouse record);

    BasChannelWarehouse selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasChannelWarehouse record);

    int updateByPrimaryKey(BasChannelWarehouse record);

    List<BasChannelWarehouse>  selectListWarehouseList(@Param("id") Integer id);
}