package com.szmsd.bas.dao;

import com.szmsd.bas.domain.BasCarrierKeywordData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BasCarrierKeywordDataMapper {
    int deleteByPrimaryKey(Integer id);
    int insertSelective(BasCarrierKeywordData record);

    List<BasCarrierKeywordData> selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasCarrierKeywordData record);

    int updateByPrimaryKey(BasCarrierKeywordData record);
}