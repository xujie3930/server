package com.szmsd.bas.dao;


import com.szmsd.bas.domain.BasTranslate;
import org.apache.ibatis.annotations.Param;

public interface BasTranslateMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasTranslate record);

    int insertSelective(BasTranslate record);

    BasTranslate selectByPrimaryKey(@Param("query") String query);

    int updateByPrimaryKeySelective(BasTranslate record);

    int updateByPrimaryKey(BasTranslate record);
}