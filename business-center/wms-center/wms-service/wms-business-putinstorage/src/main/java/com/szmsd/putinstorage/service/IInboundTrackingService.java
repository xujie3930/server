package com.szmsd.putinstorage.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.putinstorage.domain.InboundTracking;
import com.szmsd.putinstorage.domain.vo.InboundTrackingExportVO;

import java.util.List;

/**
 * <p>
 * 入库物流到货记录 服务类
 * </p>
 *
 * @author 11
 * @since 2021-09-06
 */
public interface IInboundTrackingService extends IService<InboundTracking> {

    /**
     * 查询入库物流到货记录模块
     *
     * @param id 入库物流到货记录模块ID
     * @return 入库物流到货记录模块
     */
    InboundTracking selectInboundTrackingById(String id);

    /**
     * 查询入库物流到货记录模块列表
     *
     * @param inboundTracking 入库物流到货记录模块
     * @return 入库物流到货记录模块集合
     */
    List<InboundTracking> selectInboundTrackingList(InboundTracking inboundTracking);

    /**
     * 导出揽收记录
     *
     * @param batchIdList
     * @return
     */
    List<InboundTrackingExportVO> selectInboundTrackingList(List<String> orderNoList);

    /**
     * 新增入库物流到货记录模块
     *
     * @param inboundTracking 入库物流到货记录模块
     * @return 结果
     */
    int insertInboundTracking(InboundTracking inboundTracking);

    /**
     * 修改入库物流到货记录模块
     *
     * @param inboundTracking 入库物流到货记录模块
     * @return 结果
     */
    int updateInboundTracking(InboundTracking inboundTracking);

    /**
     * 批量删除入库物流到货记录模块
     *
     * @param ids 需要删除的入库物流到货记录模块ID
     * @return 结果
     */
    int deleteInboundTrackingByIds(List<String> ids);

    /**
     * 删除入库物流到货记录模块信息
     *
     * @param id 入库物流到货记录模块ID
     * @return 结果
     */
    int deleteInboundTrackingById(String id);

}

