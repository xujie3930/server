package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.BasTrackingPush;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasTrackingPushMapper {
    int deleteByPrimaryKey(@Param("orderNo") String orderNo);

    int insert(BasTrackingPush record);

    int insertSelective(BasTrackingPush record);

    List<BasTrackingPush> selectByPrimaryKey();

    int updateByPrimaryKeySelective(BasTrackingPush record);

    int updateByPrimaryKey(BasTrackingPush record);
}