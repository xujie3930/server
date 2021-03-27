package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundDetail;

import java.util.List;

/**
 * <p>
 * 出库单明细 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
public interface IDelOutboundDetailService extends IService<DelOutboundDetail> {

    /**
     * 查询出库单明细模块
     *
     * @param id 出库单明细模块ID
     * @return 出库单明细模块
     */
    DelOutboundDetail selectDelOutboundDetailById(String id);

    /**
     * 查询出库单明细模块列表
     *
     * @param delOutboundDetail 出库单明细模块
     * @return 出库单明细模块集合
     */
    List<DelOutboundDetail> selectDelOutboundDetailList(DelOutboundDetail delOutboundDetail);

    /**
     * 新增出库单明细模块
     *
     * @param delOutboundDetail 出库单明细模块
     * @return 结果
     */
    int insertDelOutboundDetail(DelOutboundDetail delOutboundDetail);

    /**
     * 修改出库单明细模块
     *
     * @param delOutboundDetail 出库单明细模块
     * @return 结果
     */
    int updateDelOutboundDetail(DelOutboundDetail delOutboundDetail);

    /**
     * 批量删除出库单明细模块
     *
     * @param ids 需要删除的出库单明细模块ID
     * @return 结果
     */
    int deleteDelOutboundDetailByIds(List<String> ids);

    /**
     * 删除出库单明细模块信息
     *
     * @param id 出库单明细模块ID
     * @return 结果
     */
    int deleteDelOutboundDetailById(String id);

    /**
     * 根据orderNo查询
     *
     * @param orderNo orderNo
     * @return List<DelOutboundDetail>
     */
    List<DelOutboundDetail> listByOrderNo(String orderNo);

    /**
     * 根据orderNo查询
     *
     * @param orderNos orderNos
     * @return List<DelOutboundDetail>
     */
    List<DelOutboundDetail> listByOrderNos(List<String> orderNos);

}

