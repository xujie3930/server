package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundAddress;

import java.util.List;

/**
 * <p>
 * 出库单地址 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
public interface IDelOutboundAddressService extends IService<DelOutboundAddress> {

    /**
     * 查询出库单地址模块
     *
     * @param id 出库单地址模块ID
     * @return 出库单地址模块
     */
    DelOutboundAddress selectDelOutboundAddressById(String id);

    /**
     * 查询出库单地址模块列表
     *
     * @param delOutboundAddress 出库单地址模块
     * @return 出库单地址模块集合
     */
    List<DelOutboundAddress> selectDelOutboundAddressList(DelOutboundAddress delOutboundAddress);

    /**
     * 新增出库单地址模块
     *
     * @param delOutboundAddress 出库单地址模块
     * @return 结果
     */
    int insertDelOutboundAddress(DelOutboundAddress delOutboundAddress);

    /**
     * 修改出库单地址模块
     *
     * @param delOutboundAddress 出库单地址模块
     * @return 结果
     */
    int updateDelOutboundAddress(DelOutboundAddress delOutboundAddress);

    /**
     * 批量删除出库单地址模块
     *
     * @param ids 需要删除的出库单地址模块ID
     * @return 结果
     */
    int deleteDelOutboundAddressByIds(List<String> ids);

    /**
     * 删除出库单地址模块信息
     *
     * @param id 出库单地址模块ID
     * @return 结果
     */
    int deleteDelOutboundAddressById(String id);

    /**
     * 根据orderNo查询地址
     *
     * @param orderNo orderNo
     * @return DelOutboundAddress
     */
    DelOutboundAddress getByOrderNo(String orderNo);
}

