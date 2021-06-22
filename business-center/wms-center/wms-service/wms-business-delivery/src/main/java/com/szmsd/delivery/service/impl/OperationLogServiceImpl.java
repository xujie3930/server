package com.szmsd.delivery.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.szmsd.delivery.domain.OperationLog;
import com.szmsd.delivery.mapper.OperationLogMapper;
import com.szmsd.delivery.service.IOperationLogService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 业务操作日志 服务实现类
 * </p>
 *
 * @author asd
 * @since 2021-06-21
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {


    /**
     * 查询业务操作日志模块
     *
     * @param id 业务操作日志模块ID
     * @return 业务操作日志模块
     */
    @Override
    public OperationLog selectOperationLogById(String id) {
        return baseMapper.selectById(id);
    }

    /**
     * 查询业务操作日志模块列表
     *
     * @param operationLog 业务操作日志模块
     * @return 业务操作日志模块
     */
    @Override
    public List<OperationLog> selectOperationLogList(OperationLog operationLog) {
        QueryWrapper<OperationLog> where = new QueryWrapper<>();
        where.eq("invoice_no", operationLog.getInvoiceNo());
        where.orderByDesc("create_time");
        return baseMapper.selectList(where);
    }

    /**
     * 新增业务操作日志模块
     *
     * @param operationLog 业务操作日志模块
     * @return 结果
     */
    @Override
    public int insertOperationLog(OperationLog operationLog) {
        return baseMapper.insert(operationLog);
    }

    /**
     * 修改业务操作日志模块
     *
     * @param operationLog 业务操作日志模块
     * @return 结果
     */
    @Override
    public int updateOperationLog(OperationLog operationLog) {
        return baseMapper.updateById(operationLog);
    }

    /**
     * 批量删除业务操作日志模块
     *
     * @param ids 需要删除的业务操作日志模块ID
     * @return 结果
     */
    @Override
    public int deleteOperationLogByIds(List<String> ids) {
        return baseMapper.deleteBatchIds(ids);
    }

    /**
     * 删除业务操作日志模块信息
     *
     * @param id 业务操作日志模块ID
     * @return 结果
     */
    @Override
    public int deleteOperationLogById(String id) {
        return baseMapper.deleteById(id);
    }


}

