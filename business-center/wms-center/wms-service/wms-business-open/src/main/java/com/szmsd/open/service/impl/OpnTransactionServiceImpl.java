package com.szmsd.open.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.open.domain.OpnTransaction;
import com.szmsd.open.enums.OpnTransactionStateEnum;
import com.szmsd.open.mapper.OpnTransactionMapper;
import com.szmsd.open.service.IOpnTransactionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 业务处理 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-03-08
 */
@Service
public class OpnTransactionServiceImpl extends ServiceImpl<OpnTransactionMapper, OpnTransaction> implements IOpnTransactionService {


    /**
     * 查询业务处理模块
     *
     * @param id 业务处理模块ID
     * @return 业务处理模块
     */
    @Override
    public OpnTransaction selectOpnTransactionById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询业务处理模块列表
     *
     * @param opnTransaction 业务处理模块
     * @return 业务处理模块
     */
    @Override
    public List<OpnTransaction> selectOpnTransactionList(OpnTransaction opnTransaction) {
        QueryWrapper<OpnTransaction> where = new QueryWrapper<OpnTransaction>();
        return baseMapper.selectList(where);
    }

    /**
     * 新增业务处理模块
     *
     * @param opnTransaction 业务处理模块
     * @return 结果
     */
    @Override
    public int insertOpnTransaction(OpnTransaction opnTransaction) {
        return baseMapper.insert(opnTransaction);
    }

    /**
     * 修改业务处理模块
     *
     * @param opnTransaction 业务处理模块
     * @return 结果
     */
    @Override
    public int updateOpnTransaction(OpnTransaction opnTransaction) {
        return baseMapper.updateById(opnTransaction);
    }

    /**
     * 批量删除业务处理模块
     *
     * @param ids 需要删除的业务处理模块ID
     * @return 结果
     */
    @Override
    public int deleteOpnTransactionByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除业务处理模块信息
     *
     * @param id 业务处理模块ID
     * @return 结果
     */
    @Override
    public int deleteOpnTransactionById(String id) {
        return baseMapper.deleteById(id);
    }

    @Override
    public boolean hasRep(String requestUri, String transactionId) {
        LambdaQueryWrapper<OpnTransaction> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(OpnTransaction::getRequestUri, requestUri);
        queryWrapper.eq(OpnTransaction::getTransactionId, transactionId);
        queryWrapper.eq(OpnTransaction::getState, OpnTransactionStateEnum.REP);
        return this.count(queryWrapper) > 0;
    }

    @Transactional
    @Override
    public void add(String traceId, String requestUri, String transactionId) {
        OpnTransaction opnTransaction = new OpnTransaction();
        opnTransaction.setTraceId(traceId);
        opnTransaction.setRequestUri(requestUri);
        opnTransaction.setTransactionId(transactionId);
        opnTransaction.setState(OpnTransactionStateEnum.REQ.name());
        this.save(opnTransaction);
    }

    @Transactional
    @Override
    public void onRep(String traceId) {
        LambdaUpdateWrapper<OpnTransaction> updateWrapper = Wrappers.lambdaUpdate();
        updateWrapper.set(OpnTransaction::getState, OpnTransactionStateEnum.REP);
        updateWrapper.eq(OpnTransaction::getTraceId, traceId);
        this.update(updateWrapper);
    }
}

