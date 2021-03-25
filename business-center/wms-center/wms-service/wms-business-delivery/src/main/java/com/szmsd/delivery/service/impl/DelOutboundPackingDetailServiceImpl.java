package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.DelOutboundPackingDetail;
import com.szmsd.delivery.mapper.DelOutboundPackingDetailMapper;
import com.szmsd.delivery.service.IDelOutboundPackingDetailService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 装箱明细信息 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-23
 */
@Service
public class DelOutboundPackingDetailServiceImpl extends ServiceImpl<DelOutboundPackingDetailMapper, DelOutboundPackingDetail> implements IDelOutboundPackingDetailService {


    /**
     * 查询装箱明细信息模块
     *
     * @param id 装箱明细信息模块ID
     * @return 装箱明细信息模块
     */
    @Override
    public DelOutboundPackingDetail selectDelOutboundPackingDetailById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询装箱明细信息模块列表
     *
     * @param delOutboundPackingDetail 装箱明细信息模块
     * @return 装箱明细信息模块
     */
    @Override
    public List<DelOutboundPackingDetail> selectDelOutboundPackingDetailList(DelOutboundPackingDetail delOutboundPackingDetail) {
        QueryWrapper<DelOutboundPackingDetail> where = new QueryWrapper<DelOutboundPackingDetail>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增装箱明细信息模块
     *
     * @param delOutboundPackingDetail 装箱明细信息模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundPackingDetail(DelOutboundPackingDetail delOutboundPackingDetail) {
        return baseMapper.insert(delOutboundPackingDetail);
    }

    /**
     * 修改装箱明细信息模块
     *
     * @param delOutboundPackingDetail 装箱明细信息模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundPackingDetail(DelOutboundPackingDetail delOutboundPackingDetail) {
        return baseMapper.updateById(delOutboundPackingDetail);
    }

    /**
     * 批量删除装箱明细信息模块
     *
     * @param ids 需要删除的装箱明细信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundPackingDetailByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除装箱明细信息模块信息
     *
     * @param id 装箱明细信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundPackingDetailById(String id) {
        return baseMapper.deleteById(id);
    }


}

