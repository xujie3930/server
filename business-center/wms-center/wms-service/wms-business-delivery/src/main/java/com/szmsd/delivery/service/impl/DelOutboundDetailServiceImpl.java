package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutboundDetail;
import com.szmsd.delivery.mapper.DelOutboundDetailMapper;
import com.szmsd.delivery.service.IDelOutboundDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 出库单明细 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-05
 */
@Service
public class DelOutboundDetailServiceImpl extends ServiceImpl<DelOutboundDetailMapper, DelOutboundDetail> implements IDelOutboundDetailService {

    /**
     * 查询出库单明细模块
     *
     * @param id 出库单明细模块ID
     * @return 出库单明细模块
     */
    @Override
    public DelOutboundDetail selectDelOutboundDetailById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询出库单明细模块列表
     *
     * @param delOutboundDetail 出库单明细模块
     * @return 出库单明细模块
     */
    @Override
    public List<DelOutboundDetail> selectDelOutboundDetailList(DelOutboundDetail delOutboundDetail) {
        QueryWrapper<DelOutboundDetail> where = new QueryWrapper<>();
        if(StringUtils.isNotBlank(delOutboundDetail.getOrderNo())) {
            where.eq("order_no",delOutboundDetail.getOrderNo());
        }
        return baseMapper.selectList(where);
    }

    /**
     * 新增出库单明细模块
     *
     * @param delOutboundDetail 出库单明细模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundDetail(DelOutboundDetail delOutboundDetail) {
        return baseMapper.insert(delOutboundDetail);
    }

    /**
     * 修改出库单明细模块
     *
     * @param delOutboundDetail 出库单明细模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundDetail(DelOutboundDetail delOutboundDetail) {
        return baseMapper.updateById(delOutboundDetail);
    }

    /**
     * 批量删除出库单明细模块
     *
     * @param ids 需要删除的出库单明细模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundDetailByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除出库单明细模块信息
     *
     * @param id 出库单明细模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundDetailById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public List<DelOutboundDetail> listByOrderNo(String orderNo) {
        LambdaQueryWrapper<DelOutboundDetail> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(DelOutboundDetail::getOrderNo, orderNo);
        return this.list(queryWrapper);
    }

    @Override
    public List<DelOutboundDetail> listByOrderNos(List<String> orderNos) {
        LambdaQueryWrapper<DelOutboundDetail> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(DelOutboundDetail::getOrderNo, orderNos);
        return this.list(queryWrapper);
    }
}

