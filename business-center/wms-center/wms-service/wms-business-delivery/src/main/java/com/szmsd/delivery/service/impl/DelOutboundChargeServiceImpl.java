package com.szmsd.delivery.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutboundCharge;
import com.szmsd.delivery.mapper.DelOutboundChargeMapper;
import com.szmsd.delivery.service.IDelOutboundChargeService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.net.Inet4Address;
import java.net.UnknownHostException;
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
public class DelOutboundChargeServiceImpl extends ServiceImpl<DelOutboundChargeMapper, DelOutboundCharge> implements IDelOutboundChargeService, InitializingBean {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundChargeServiceImpl.class);

    private Snowflake snowflake;
    @Value(value = "${server.port:0}")
    private int port;

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
        delOutboundCharge.setId(this.snowflake.nextId());
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
        DelOutboundCharge delOutboundCharge = charges.get(0);
        // 先根据单号删除
        this.clearCharges(delOutboundCharge.getOrderNo());
        // 设置ID
        charges.forEach(value -> value.setId(this.snowflake.nextId()));
        // 批量保存
        this.saveBatch(charges);
    }

    @Override
    public List<DelOutboundCharge> listCharges(String orderNo) {
        LambdaQueryWrapper<DelOutboundCharge> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundCharge::getOrderNo, orderNo);
        return this.list(queryWrapper);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void clearCharges(String orderNo) {
        LambdaQueryWrapper<DelOutboundCharge> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundCharge::getOrderNo, orderNo);
        // 判断有没有数据
        if (super.count(queryWrapper) > 0) {
            // 有数据就删除
            super.remove(queryWrapper);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        int dataCenterId = 1;
        if (this.port > 0) {
            dataCenterId = this.port % 32;
        }
        this.snowflake = new Snowflake(getWorkId(), dataCenterId);
    }

    private Long getWorkId() {
        try {
            String hostAddress = Inet4Address.getLocalHost().getHostAddress();
            this.logger.info("当前机器IP：{}，当前服务端口：{}", hostAddress, this.port);
            int[] ints = StringUtils.toCodePoints(hostAddress);
            int sums = 0;
            for (int b : ints) {
                sums += b;
            }
            return (long) (sums % 32);
        } catch (UnknownHostException e) {
            this.logger.error(e.getMessage(), e);
            // 如果获取失败，则使用随机数备用
            return RandomUtils.nextLong(0, 31);
        }
    }
}

