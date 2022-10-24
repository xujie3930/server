package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.DelQueryServiceError;

public interface DelQueryServiceErrorMapper {
    int deleteByPrimaryKey();

    int insert(DelQueryServiceError record);

    int insertSelective(DelQueryServiceError record);

    DelQueryServiceError selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DelQueryServiceError record);

    int updateByPrimaryKey(DelQueryServiceError record);
}