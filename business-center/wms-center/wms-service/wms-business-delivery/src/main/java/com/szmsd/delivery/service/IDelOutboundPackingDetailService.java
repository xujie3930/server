package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundPackingDetail;

import java.util.List;

/**
 * <p>
 * 装箱明细信息 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-23
 */
public interface IDelOutboundPackingDetailService extends IService<DelOutboundPackingDetail> {

    /**
     * 查询装箱明细信息模块
     *
     * @param id 装箱明细信息模块ID
     * @return 装箱明细信息模块
     */
    DelOutboundPackingDetail selectDelOutboundPackingDetailById(String id);

    /**
     * 查询装箱明细信息模块列表
     *
     * @param delOutboundPackingDetail 装箱明细信息模块
     * @return 装箱明细信息模块集合
     */
    List<DelOutboundPackingDetail> selectDelOutboundPackingDetailList(DelOutboundPackingDetail delOutboundPackingDetail);

    /**
     * 新增装箱明细信息模块
     *
     * @param delOutboundPackingDetail 装箱明细信息模块
     * @return 结果
     */
    int insertDelOutboundPackingDetail(DelOutboundPackingDetail delOutboundPackingDetail);

    /**
     * 修改装箱明细信息模块
     *
     * @param delOutboundPackingDetail 装箱明细信息模块
     * @return 结果
     */
    int updateDelOutboundPackingDetail(DelOutboundPackingDetail delOutboundPackingDetail);

    /**
     * 批量删除装箱明细信息模块
     *
     * @param ids 需要删除的装箱明细信息模块ID
     * @return 结果
     */
    int deleteDelOutboundPackingDetailByIds(List<String> ids);

    /**
     * 删除装箱明细信息模块信息
     *
     * @param id 装箱明细信息模块ID
     * @return 结果
     */
    int deleteDelOutboundPackingDetailById(String id);

}

