package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.DelTimelinessConfig;

import java.util.List;

public interface DelTimelinessConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DelTimelinessConfig record);

    int insertSelective(DelTimelinessConfig record);

    List<DelTimelinessConfig> selectByPrimaryKey();

    int updateByPrimaryKeySelective(DelTimelinessConfig record);

    int updateByPrimaryKey(DelTimelinessConfig record);
}