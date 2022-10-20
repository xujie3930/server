package com.szmsd.bas.mapper;


import com.szmsd.bas.domain.BasEmail;

import java.util.List;

public interface BasEmailMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasEmail record);

    int insertSelective(BasEmail record);

    List<BasEmail> selectByPrimaryKey();

    int updateByPrimaryKeySelective(BasEmail record);

    int updateByPrimaryKey();
}