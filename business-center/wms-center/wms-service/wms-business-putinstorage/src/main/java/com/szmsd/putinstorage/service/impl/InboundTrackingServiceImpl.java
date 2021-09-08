package com.szmsd.putinstorage.service.impl;

import com.szmsd.putinstorage.domain.InboundTracking;
import com.szmsd.putinstorage.mapper.InboundTrackingMapper;
import com.szmsd.putinstorage.service.IInboundTrackingService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.common.core.domain.R;

import java.util.List;

/**
 * <p>
 * 入库物流到货记录 服务实现类
 * </p>
 *
 * @author 11
 * @since 2021-09-06
 */
@Service
public class InboundTrackingServiceImpl extends ServiceImpl<InboundTrackingMapper, InboundTracking> implements IInboundTrackingService {


    /**
     * 查询入库物流到货记录模块
     *
     * @param id 入库物流到货记录模块ID
     * @return 入库物流到货记录模块
     */
    @Override
    public InboundTracking selectInboundTrackingById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询入库物流到货记录模块列表
     *
     * @param inboundTracking 入库物流到货记录模块
     * @return 入库物流到货记录模块
     */
    @Override
    public List<InboundTracking> selectInboundTrackingList(InboundTracking inboundTracking) {
        QueryWrapper<InboundTracking> where = new QueryWrapper<InboundTracking>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增入库物流到货记录模块
     *
     * @param inboundTracking 入库物流到货记录模块
     * @return 结果
     */
    @Override
    public int insertInboundTracking(InboundTracking inboundTracking) {
        return baseMapper.insert(inboundTracking);
    }

    /**
     * 修改入库物流到货记录模块
     *
     * @param inboundTracking 入库物流到货记录模块
     * @return 结果
     */
    @Override
    public int updateInboundTracking(InboundTracking inboundTracking) {
        return baseMapper.updateById(inboundTracking);
    }

    /**
     * 批量删除入库物流到货记录模块
     *
     * @param ids 需要删除的入库物流到货记录模块ID
     * @return 结果
     */
    @Override
    public int deleteInboundTrackingByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除入库物流到货记录模块信息
     *
     * @param id 入库物流到货记录模块ID
     * @return 结果
     */
    @Override
    public int deleteInboundTrackingById(String id) {
        return baseMapper.deleteById(id);
    }


}

