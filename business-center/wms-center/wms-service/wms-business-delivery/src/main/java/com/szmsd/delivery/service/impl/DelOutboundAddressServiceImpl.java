package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutboundAddress;
import com.szmsd.delivery.mapper.DelOutboundAddressMapper;
import com.szmsd.delivery.service.IDelOutboundAddressService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 出库单地址 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Service
public class DelOutboundAddressServiceImpl extends ServiceImpl<DelOutboundAddressMapper, DelOutboundAddress> implements IDelOutboundAddressService {

    /**
     * 查询出库单地址模块
     *
     * @param id 出库单地址模块ID
     * @return 出库单地址模块
     */
    @Override
    public DelOutboundAddress selectDelOutboundAddressById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询出库单地址模块列表
     *
     * @param delOutboundAddress 出库单地址模块
     * @return 出库单地址模块
     */
    @Override
    public List<DelOutboundAddress> selectDelOutboundAddressList(DelOutboundAddress delOutboundAddress) {
        QueryWrapper<DelOutboundAddress> where = new QueryWrapper<DelOutboundAddress>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增出库单地址模块
     *
     * @param delOutboundAddress 出库单地址模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundAddress(DelOutboundAddress delOutboundAddress) {
        return baseMapper.insert(delOutboundAddress);
    }

    /**
     * 修改出库单地址模块
     *
     * @param delOutboundAddress 出库单地址模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundAddress(DelOutboundAddress delOutboundAddress) {
        return baseMapper.updateById(delOutboundAddress);
    }

    /**
     * 批量删除出库单地址模块
     *
     * @param ids 需要删除的出库单地址模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundAddressByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除出库单地址模块信息
     *
     * @param id 出库单地址模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundAddressById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public DelOutboundAddress getByOrderNo(String orderNo) {
        LambdaQueryWrapper<DelOutboundAddress> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundAddress::getOrderNo, orderNo);
        return this.getOne(queryWrapper);
    }
}

