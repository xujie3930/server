package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundCompleted;

import java.util.List;

/**
 * <p>
 * 出库单完成记录 服务类
 * </p>
 *
 * @author asd
 * @since 2021-04-06
 */
public interface IDelOutboundCompletedService extends IService<DelOutboundCompleted> {

    /**
     * 查询出库单完成记录模块
     *
     * @param id 出库单完成记录模块ID
     * @return 出库单完成记录模块
     */
    DelOutboundCompleted selectDelOutboundCompletedById(String id);

    /**
     * 查询出库单完成记录模块列表
     *
     * @param delOutboundCompleted 出库单完成记录模块
     * @return 出库单完成记录模块集合
     */
    List<DelOutboundCompleted> selectDelOutboundCompletedList(DelOutboundCompleted delOutboundCompleted);

    /**
     * 新增出库单完成记录模块
     *
     * @param delOutboundCompleted 出库单完成记录模块
     * @return 结果
     */
    int insertDelOutboundCompleted(DelOutboundCompleted delOutboundCompleted);

    /**
     * 修改出库单完成记录模块
     *
     * @param delOutboundCompleted 出库单完成记录模块
     * @return 结果
     */
    int updateDelOutboundCompleted(DelOutboundCompleted delOutboundCompleted);

    /**
     * 批量删除出库单完成记录模块
     *
     * @param ids 需要删除的出库单完成记录模块ID
     * @return 结果
     */
    int deleteDelOutboundCompletedByIds(List<String> ids);

    /**
     * 删除出库单完成记录模块信息
     *
     * @param id 出库单完成记录模块ID
     * @return 结果
     */
    int deleteDelOutboundCompletedById(String id);

    /**
     * 新增完成的出库单记录
     *
     * @param orderNos      orderNos
     * @param operationType operationType
     */
    void add(List<String> orderNos, String operationType);

    /**
     * 处理失败
     *
     * @param id id
     */
    void fail(Long id);

    /**
     * 处理成功
     *
     * @param id id
     */
    void success(Long id);
}

