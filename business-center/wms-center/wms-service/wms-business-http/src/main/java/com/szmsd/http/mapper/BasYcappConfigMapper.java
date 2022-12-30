package com.szmsd.http.mapper;


import com.szmsd.http.domain.YcAppParameter;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasYcappConfigMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(YcAppParameter record);

    int insertSelective(YcAppParameter record);

    List<YcAppParameter> selectByPrimaryKey();

    int updateByPrimaryKeySelective(YcAppParameter record);

    int updateByPrimaryKey(YcAppParameter record);
}