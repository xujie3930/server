package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.DelOutboundCharge;

import java.util.List;

/**
 * <p>
 * 出库单费用明细 服务类
 * </p>
 *
 * @author asd
 * @since 2021-04-01
 */
public interface IDelOutboundChargeService extends IService<DelOutboundCharge> {

    /**
     * 查询出库单费用明细模块
     *
     * @param id 出库单费用明细模块ID
     * @return 出库单费用明细模块
     */
    DelOutboundCharge selectDelOutboundChargeById(String id);

    /**
     * 查询出库单费用明细模块列表
     *
     * @param delOutboundCharge 出库单费用明细模块
     * @return 出库单费用明细模块集合
     */
    List<DelOutboundCharge> selectDelOutboundChargeList(DelOutboundCharge delOutboundCharge);

    /**
     * 新增出库单费用明细模块
     *
     * @param delOutboundCharge 出库单费用明细模块
     * @return 结果
     */
    int insertDelOutboundCharge(DelOutboundCharge delOutboundCharge);

    /**
     * 修改出库单费用明细模块
     *
     * @param delOutboundCharge 出库单费用明细模块
     * @return 结果
     */
    int updateDelOutboundCharge(DelOutboundCharge delOutboundCharge);

    /**
     * 批量删除出库单费用明细模块
     *
     * @param ids 需要删除的出库单费用明细模块ID
     * @return 结果
     */
    int deleteDelOutboundChargeByIds(List<String> ids);

    /**
     * 删除出库单费用明细模块信息
     *
     * @param id 出库单费用明细模块ID
     * @return 结果
     */
    int deleteDelOutboundChargeById(String id);

    /**
     * 保存费用信息
     *
     * @param charges charges
     */
    void saveCharges(List<DelOutboundCharge> charges);

    /**
     * 查询费用信息
     *
     * @param orderNo orderNo
     * @return List<DelOutboundCharge>
     */
    List<DelOutboundCharge> listCharges(String orderNo);

    /**
     * 清空费用信息
     *
     * @param orderNo orderNo
     */
    void clearCharges(String orderNo);
}

