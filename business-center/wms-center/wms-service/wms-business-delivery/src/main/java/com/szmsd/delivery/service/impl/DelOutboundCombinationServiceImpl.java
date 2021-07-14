package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.utils.bean.BeanMapperUtil;
import com.szmsd.delivery.domain.DelOutboundCombination;
import com.szmsd.delivery.dto.DelOutboundCombinationDto;
import com.szmsd.delivery.mapper.DelOutboundCombinationMapper;
import com.szmsd.delivery.service.IDelOutboundCombinationService;
import com.szmsd.delivery.vo.DelOutboundCombinationVO;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 出库单组合信息 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-07-02
 */
@Service
public class DelOutboundCombinationServiceImpl extends ServiceImpl<DelOutboundCombinationMapper, DelOutboundCombination> implements IDelOutboundCombinationService {


    /**
     * 查询出库单组合信息模块
     *
     * @param id 出库单组合信息模块ID
     * @return 出库单组合信息模块
     */
    @Override
    public DelOutboundCombination selectDelOutboundCombinationById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询出库单组合信息模块列表
     *
     * @param delOutboundCombination 出库单组合信息模块
     * @return 出库单组合信息模块
     */
    @Override
    public List<DelOutboundCombination> selectDelOutboundCombinationList(DelOutboundCombination delOutboundCombination) {
        QueryWrapper<DelOutboundCombination> where = new QueryWrapper<DelOutboundCombination>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增出库单组合信息模块
     *
     * @param delOutboundCombination 出库单组合信息模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundCombination(DelOutboundCombination delOutboundCombination) {
        return baseMapper.insert(delOutboundCombination);
    }

    /**
     * 修改出库单组合信息模块
     *
     * @param delOutboundCombination 出库单组合信息模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundCombination(DelOutboundCombination delOutboundCombination) {
        return baseMapper.updateById(delOutboundCombination);
    }

    /**
     * 批量删除出库单组合信息模块
     *
     * @param ids 需要删除的出库单组合信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundCombinationByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除出库单组合信息模块信息
     *
     * @param id 出库单组合信息模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundCombinationById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public void save(String orderNo, List<DelOutboundCombinationDto> combinations) {
        if (CollectionUtils.isEmpty(combinations)) {
            return;
        }
        List<DelOutboundCombination> outboundCombinations = new ArrayList<>();
        for (DelOutboundCombinationDto combination : combinations) {
            DelOutboundCombination outboundCombination = BeanMapperUtil.map(combination, DelOutboundCombination.class);
            outboundCombination.setOrderNo(orderNo);
            outboundCombinations.add(outboundCombination);
        }
        this.saveBatch(outboundCombinations);
    }

    @Transactional
    @Override
    public void update(String orderNo, List<DelOutboundCombinationDto> combinations) {
        this.deleted(orderNo);
        this.save(orderNo, combinations);
    }

    @Transactional
    @Override
    public void deleted(String orderNo) {
        // 删除
        this.remove(Wrappers.<DelOutboundCombination>lambdaQuery().eq(DelOutboundCombination::getOrderNo, orderNo));
    }

    @Transactional
    @Override
    public void deleted(List<String> orderNos) {
        // 删除
        this.remove(Wrappers.<DelOutboundCombination>lambdaQuery().in(DelOutboundCombination::getOrderNo, orderNos));
    }

    @Override
    public List<DelOutboundCombinationVO> listByOrderNo(String orderNo) {
        // 查询
        List<DelOutboundCombination> combinationList = this.list(Wrappers.<DelOutboundCombination>lambdaQuery().eq(DelOutboundCombination::getOrderNo, orderNo));
        if (CollectionUtils.isEmpty(combinationList)) {
            return Collections.emptyList();
        }
        return BeanMapperUtil.mapList(combinationList, DelOutboundCombinationVO.class);
    }
}

