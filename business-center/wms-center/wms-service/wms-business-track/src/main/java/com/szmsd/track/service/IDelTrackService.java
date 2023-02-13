package com.szmsd.track.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.common.core.domain.R;
import com.szmsd.track.domain.Track;
import com.szmsd.track.dto.*;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
public interface IDelTrackService extends IService<Track> {

    void addData(Track delTrack);

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    Track selectDelTrackById(String id);

    /**
     * 查询模块列表
     *
     * @param delTrack 模块
     * @return 模块集合
     */
    List<Track> selectDelTrackList(Track delTrack);

    /**
     * 新增模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    int insertDelTrack(Track delTrack);

    /**
     * 修改模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    int updateDelTrack(Track delTrack);

    /**
     * 批量删除模块
     *
     * @param ids 需要删除的模块ID
     * @return 结果
     */
    int deleteDelTrackByIds(List<String> ids);

    /**
     * 删除模块信息
     *
     * @param id 模块ID
     * @return 结果
     */
    int deleteDelTrackById(String id);

    /**
     * trackingYee 推送的轨迹处理
     * @param trackingYeeTraceDto
     */
    void traceCallback(TrackingYeeTraceDto trackingYeeTraceDto);

    /**
     * 获取轨迹分析饼状图数据
     * @param requestDto
     * @return
     */
    List<TrackAnalysisDto> getTrackAnalysis(TrackAnalysisRequestDto requestDto);

    /**
     * 获取轨迹分析饼柱状图数据
     * @param requestDto
     * @return
     */
    List<TrackAnalysisDto> getProductServiceAnalysis(TrackAnalysisRequestDto requestDto);

    /**
     * 获取轨迹分析导出数据
     * @param requestDto
     * @return
     */
    List<TrackAnalysisExportDto> getAnalysisExportData(TrackAnalysisRequestDto requestDto);


    R<TrackMainCommonDto> commonTrackList(List<String> orderNos);

    void saveOrUpdateTrack(Track delTrack);

    void pushTY(TrackTyRequestLogDto requestLogDto);

    R<Integer> checkTrackDoc(String orderNo,Integer trackStayDays);

}

