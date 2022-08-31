package com.szmsd.bas.mapper;


import com.szmsd.bas.domain.BasMeteringConfigData;

import java.util.List;

public interface BasMeteringConfigDataMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasMeteringConfigData record);

    int insertSelective(BasMeteringConfigData record);

    List<BasMeteringConfigData> selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasMeteringConfigData record);

    int updateByPrimaryKey(BasMeteringConfigData record);
}