package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.delivery.domain.DelTrack;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.dto.DelOutboundDto;
import com.szmsd.delivery.dto.DelOutboundReportQueryDto;
import com.szmsd.delivery.dto.TrackAnalysisDto;
import com.szmsd.delivery.dto.TrackAnalysisRequestDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
public interface DelTrackMapper extends BaseMapper<DelTrack> {

    List<TrackAnalysisDto> getTrackAnalysis(@Param(Constants.WRAPPER) QueryWrapper<TrackAnalysisRequestDto> queryWrapper);

    List<TrackAnalysisDto> getProductServiceAnalysis(@Param(Constants.WRAPPER) QueryWrapper<TrackAnalysisRequestDto> queryWrapper);
}
