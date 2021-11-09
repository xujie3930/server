package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.mapper.DelOutboundChargeMapper;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 出库单费用明细 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-04-01
 */
@Service
public class DelOutboundChargeServiceImpl extends ServiceImpl<DelOutboundChargeMapper, DelOutboundCharge> implements IDelOutboundChargeService {


    /**
     * 查询出库单费用明细模块
     *
     * @param id 出库单费用明细模块ID
     * @return 出库单费用明细模块
     */
    @Override
    public DelOutboundCharge selectDelOutboundChargeById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询出库单费用明细模块列表
     *
     * @param delOutboundCharge 出库单费用明细模块
     * @return 出库单费用明细模块
     */
    @Override
    public List<DelOutboundCharge> selectDelOutboundChargeList(DelOutboundCharge delOutboundCharge) {
        QueryWrapper<DelOutboundCharge> where = new QueryWrapper<DelOutboundCharge>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增出库单费用明细模块
     *
     * @param delOutboundCharge 出库单费用明细模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundCharge(DelOutboundCharge delOutboundCharge) {
        return baseMapper.insert(delOutboundCharge);
    }

    /**
     * 修改出库单费用明细模块
     *
     * @param delOutboundCharge 出库单费用明细模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundCharge(DelOutboundCharge delOutboundCharge) {
        return baseMapper.updateById(delOutboundCharge);
    }

    /**
     * 批量删除出库单费用明细模块
     *
     * @param ids 需要删除的出库单费用明细模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundChargeByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除出库单费用明细模块信息
     *
     * @param id 出库单费用明细模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundChargeById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void saveCharges(List<DelOutboundCharge> charges) {
        if (CollectionUtils.isEmpty(charges)) {
            return;
        }
        this.saveBatch(charges);
    }

    @Transactional
    @Override
    public List<DelOutboundCharge> listCharges(String orderNo) {
        LambdaQueryWrapper<DelOutboundCharge> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundCharge::getOrderNo, orderNo);
        return this.list(queryWrapper);
    }

    @Transactional
    @Override
    public void clearCharges(String orderNo) {
        LambdaQueryWrapper<DelOutboundCharge> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundCharge::getOrderNo, orderNo);
        this.remove(queryWrapper);
    }
}

