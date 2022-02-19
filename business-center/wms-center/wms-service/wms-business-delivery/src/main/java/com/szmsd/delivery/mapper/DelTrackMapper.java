package com.szmsd.delivery.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.delivery.domain.DelTrack;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.szmsd.delivery.dto.*;
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

    List<TrackAnalysisExportDto> getAnalysisExportData(@Param(Constants.WRAPPER) QueryWrapper<TrackAnalysisRequestDto> queryWrapper);
}
