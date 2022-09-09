package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.DelOutboundTarckOn;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DelOutboundTarckOnMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DelOutboundTarckOn record);

    int insertSelective(DelOutboundTarckOn record);

    List<DelOutboundTarckOn> selectByPrimaryKey(DelOutboundTarckOn record);

    int updateByPrimaryKeySelective(DelOutboundTarckOn record);

    int updateByPrimaryKey(DelOutboundTarckOn record);
}