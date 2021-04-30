package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.vo.DelOutboundReportListVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DelOutboundReportMapper extends BaseMapper<DelOutbound> {

    List<DelOutboundReportListVO> queryCreateData(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundReportQueryDto> queryWrapper);

    List<DelOutboundReportListVO> queryBringVerifyData(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundReportQueryDto> queryWrapper);

    List<DelOutboundReportListVO> queryOutboundData(@Param(Constants.WRAPPER) QueryWrapper<DelOutboundReportQueryDto> queryWrapper);
}
