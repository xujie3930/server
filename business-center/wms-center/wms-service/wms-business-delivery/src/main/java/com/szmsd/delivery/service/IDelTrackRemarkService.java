package com.szmsd.delivery.service;

import com.szmsd.delivery.domain.DelTrackRemark;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 轨迹备注表 服务类
 * </p>
 *
 * @author YM
 * @since 2022-05-06
 */
public interface IDelTrackRemarkService extends IService<DelTrackRemark> {

    /**
     * 查询轨迹备注表模块
     *
     * @param id 轨迹备注表模块ID
     * @return 轨迹备注表模块
     */
    DelTrackRemark selectDelTrackRemarkById(String id);

    /**
     * 查询轨迹备注表模块列表
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 轨迹备注表模块集合
     */
    List<DelTrackRemark> selectDelTrackRemarkList(DelTrackRemark delTrackRemark);

    /**
     * 新增轨迹备注表模块
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 结果
     */
    int insertDelTrackRemark(DelTrackRemark delTrackRemark);

    /**
     * 修改轨迹备注表模块
     *
     * @param delTrackRemark 轨迹备注表模块
     * @return 结果
     */
    int updateDelTrackRemark(DelTrackRemark delTrackRemark);

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

