package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.DelTimeliness;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DelTimelinessMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DelTimeliness record);

    int insertSelective(DelTimeliness record);

    DelTimeliness selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DelTimeliness record);

    int updateByPrimaryKey(DelTimeliness record);

    List<Map>  selectDelOutboundes(@Param("map") Map map);

    List<DelTimeliness>  selectDelTimeliness(DelTimeliness delTimeliness);
}