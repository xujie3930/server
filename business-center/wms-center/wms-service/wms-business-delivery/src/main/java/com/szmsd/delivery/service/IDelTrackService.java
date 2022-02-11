package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelTrack;
import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.dto.TrackingYeeTraceDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author YM
 * @since 2022-02-10
 */
public interface IDelTrackService extends IService<DelTrack> {

    /**
     * 查询模块
     *
     * @param id 模块ID
     * @return 模块
     */
    DelTrack selectDelTrackById(String id);

    /**
     * 查询模块列表
     *
     * @param delTrack 模块
     * @return 模块集合
     */
    List<DelTrack> selectDelTrackList(DelTrack delTrack);

    /**
     * 新增模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    int insertDelTrack(DelTrack delTrack);

    /**
     * 修改模块
     *
     * @param delTrack 模块
     * @return 结果
     */
    int updateDelTrack(DelTrack delTrack);

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

}

