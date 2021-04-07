package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.common.core.utils.SpringUtils;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.mapper.DelOutboundCompletedMapper;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 出库单完成记录 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-04-06
 */
@Service
public class DelOutboundCompletedServiceImpl extends ServiceImpl<DelOutboundCompletedMapper, DelOutboundCompleted> implements IDelOutboundCompletedService {


    /**
     * 查询出库单完成记录模块
     *
     * @param id 出库单完成记录模块ID
     * @return 出库单完成记录模块
     */
    @Override
    public DelOutboundCompleted selectDelOutboundCompletedById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询出库单完成记录模块列表
     *
     * @param delOutboundCompleted 出库单完成记录模块
     * @return 出库单完成记录模块
     */
    @Override
    public List<DelOutboundCompleted> selectDelOutboundCompletedList(DelOutboundCompleted delOutboundCompleted) {
        QueryWrapper<DelOutboundCompleted> where = new QueryWrapper<DelOutboundCompleted>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增出库单完成记录模块
     *
     * @param delOutboundCompleted 出库单完成记录模块
     * @return 结果
     */
    @Override
    public int insertDelOutboundCompleted(DelOutboundCompleted delOutboundCompleted) {
        return baseMapper.insert(delOutboundCompleted);
    }

    /**
     * 修改出库单完成记录模块
     *
     * @param delOutboundCompleted 出库单完成记录模块
     * @return 结果
     */
    @Override
    public int updateDelOutboundCompleted(DelOutboundCompleted delOutboundCompleted) {
        return baseMapper.updateById(delOutboundCompleted);
    }

    /**
     * 批量删除出库单完成记录模块
     *
     * @param ids 需要删除的出库单完成记录模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundCompletedByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除出库单完成记录模块信息
     *
     * @param id 出库单完成记录模块ID
     * @return 结果
     */
    @Override
    public int deleteDelOutboundCompletedById(String id) {
        return baseMapper.deleteById(id);
    }

    @Transactional
    @Override
    public void add(List<String> orderNos, String operationType) {
        if (CollectionUtils.isEmpty(orderNos)) {
            throw new CommonException("999", "出库单号不能为空");
        }
        List<DelOutboundCompleted> delOutboundCompletedList = new ArrayList<>();
        for (String orderNo : orderNos) {
            DelOutboundCompleted delOutboundCompleted = new DelOutboundCompleted();
            delOutboundCompleted.setOrderNo(orderNo);
            delOutboundCompleted.setState(DelOutboundCompletedStateEnum.INIT.getCode());
            delOutboundCompleted.setOperationType(operationType);
            delOutboundCompletedList.add(delOutboundCompleted);
        }
        this.saveBatch(delOutboundCompletedList);
    }

    @Transactional
    @Override
    public void fail(Long id) {
        DelOutboundCompleted modifyDelOutboundCompleted = new DelOutboundCompleted();
        modifyDelOutboundCompleted.setId(id);
        // 修改状态为失败
        modifyDelOutboundCompleted.setState(DelOutboundCompletedStateEnum.FAIL.getCode());
        // 处理次数累加，下一次处理时间 = 5 * 处理次数
        this.baseMapper.updateRecord(modifyDelOutboundCompleted);
    }

    @Transactional
    @Override
    public void success(Long id) {
        DelOutboundCompleted modifyDelOutboundCompleted = new DelOutboundCompleted();
        modifyDelOutboundCompleted.setId(id);
        modifyDelOutboundCompleted.setState(DelOutboundCompletedStateEnum.SUCCESS.getCode());
        this.updateById(modifyDelOutboundCompleted);
    }
}

