package com.szmsd.track.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.track.domain.TrackRemark;

import java.util.List;

/**
 * <p>
 * 轨迹备注表 服务类
 * </p>
 *
 * @author YM
 * @since 2022-05-06
 */
public interface ITrackRemarkService extends IService<TrackRemark> {

    /**
     * 查询轨迹备注表模块
     *
     * @param id 轨迹备注表模块ID
     * @return 轨迹备注表模块
     */
    TrackRemark selectDelTrackRemarkById(String id);

    /**
     * 查询轨迹备注表模块列表
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 轨迹备注表模块集合
     */
    List<TrackRemark> selectDelTrackRemarkList(TrackRemark delTrackRemark);

    /**
     * 新增轨迹备注表模块
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 结果
     */
    int insertDelTrackRemark(TrackRemark delTrackRemark);

    /**
     * 修改轨迹备注表模块
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 结果
     */
    int updateDelTrackRemark(TrackRemark delTrackRemark);

    /**
     * 批量删除轨迹备注表模块
     *
     * @param ids 需要删除的轨迹备注表模块ID
     * @return 结果
     */
    int deleteDelTrackRemarkByIds(List<String> ids);

    /**
     * 删除轨迹备注表模块信息
     *
     * @param id 轨迹备注表模块ID
     * @return 结果
     */
    int deleteDelTrackRemarkById(String id);

}

