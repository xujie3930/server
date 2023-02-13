package com.szmsd.track.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.szmsd.track.domain.Track;
import com.szmsd.track.dto.TrackAnalysisDto;
import com.szmsd.track.dto.TrackAnalysisExportDto;
import com.szmsd.track.dto.TrackAnalysisRequestDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
public interface TrackMapper extends BaseMapper<Track> {

    List<TrackAnalysisDto> getTrackAnalysis(@Param(Constants.WRAPPER) QueryWrapper<TrackAnalysisRequestDto> queryWrapper);

    List<TrackAnalysisDto> getProductServiceAnalysis(@Param(Constants.WRAPPER) QueryWrapper<TrackAnalysisRequestDto> queryWrapper);

    List<TrackAnalysisExportDto> getAnalysisExportData(@Param(Constants.WRAPPER) QueryWrapper<TrackAnalysisRequestDto> queryWrapper);

    void  deletetrack(@Param("map") Map map);
}
