package com.szmsd.delivery.mapper;


import com.szmsd.delivery.domain.BasTrackingPush;

public interface BasTrackingPushMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BasTrackingPush record);

    int insertSelective(BasTrackingPush record);

    BasTrackingPush selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasTrackingPush record);

    int updateByPrimaryKey(BasTrackingPush record);
}