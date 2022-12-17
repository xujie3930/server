package com.szmsd.finance.mapper;


import com.szmsd.finance.domain.BasRefundRequest;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BasRefundRequestMapper {
    int deleteByPrimaryKey(@Param("fid") Integer fid);

    int insert(BasRefundRequest record);

    int insertSelective(BasRefundRequest record);

    BasRefundRequest selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BasRefundRequest record);

    int updateByPrimaryKey(BasRefundRequest record);

    List<String> selectFid();
}