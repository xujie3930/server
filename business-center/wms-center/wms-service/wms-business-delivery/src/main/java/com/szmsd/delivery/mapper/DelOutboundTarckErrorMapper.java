package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.DelOutboundTarckError;

import java.util.List;

public interface DelOutboundTarckErrorMapper {
    int deleteByPrimaryKey();

    int insert(DelOutboundTarckError record);

    int insertSelective(DelOutboundTarckError record);

    List<DelOutboundTarckError> selectByPrimaryKey();

    int updateByPrimaryKeySelective(DelOutboundTarckError record);

    int updateByPrimaryKey(DelOutboundTarckError record);
}