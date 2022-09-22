package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.BasFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BasFileMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasFile record);

    int insertSelective(BasFile record);

    BasFile selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasFile record);

    int updateByPrimaryKey(BasFile record);

    Integer selectDelOutboundCount();
}