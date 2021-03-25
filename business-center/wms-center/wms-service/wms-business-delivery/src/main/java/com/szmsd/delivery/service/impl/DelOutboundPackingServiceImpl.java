package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutboundPacking;
import com.szmsd.delivery.mapper.DelOutboundPackingMapper;
import com.szmsd.delivery.service.IDelOutboundPackingService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 装箱信息 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-23
 */
@Service
public class DelOutboundPackingServiceImpl extends ServiceImpl<DelOutboundPackingMapper, DelOutboundPacking> implements IDelOutboundPackingService {


    /**
     * 查询装箱信息模块
     *
     * @param id 装箱信息模块ID
     * @return 装箱信息模块
     */
    @Override
    public DelOutboundPacking selectDelOutboundPackingById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询装箱信息模块列表
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 装箱信息模块
     */
    @Override
    public List<DelOutboundPacking> selectDelOutboundPackingList(DelOutboundPacking delOutboundPacking) {
        QueryWrapper<DelOutboundPacking> where = new QueryWrapper<DelOutboundPacking>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增装箱信息模块
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundPacking(DelOutboundPacking delOutboundPacking) {
        return baseMapper.insert(delOutboundPacking);
    }

    /**
     * 修改装箱信息模块
     *
     * @param delOutboundPacking 装箱信息模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundPacking(DelOutboundPacking delOutboundPacking) {
        return baseMapper.updateById(delOutboundPacking);
    }

    /**
     * 批量删除装箱信息模块
     *
     * @param ids 需要删除的装箱信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundPackingByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除装箱信息模块信息
     *
     * @param id 装箱信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundPackingById(String id) {
        return baseMapper.deleteById(id);
    }


}

