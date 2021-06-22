package com.szmsd.delivery.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.delivery.domain.OperationLog;

import java.util.List;

/**
 * <p>
 * 业务操作日志 服务类
 * </p>
 *
 * @author asd
 * @since 2021-06-21
 */
public interface IOperationLogService extends IService<OperationLog> {

    /**
     * 查询业务操作日志模块
     *
     * @param id 业务操作日志模块ID
     * @return 业务操作日志模块
     */
    OperationLog selectOperationLogById(String id);

    /**
     * 查询业务操作日志模块列表
     *
     * @param operationLog 业务操作日志模块
     * @return 业务操作日志模块集合
     */
    List<OperationLog> selectOperationLogList(OperationLog operationLog);

    /**
     * 新增业务操作日志模块
     *
     * @param operationLog 业务操作日志模块
     * @return 结果
     */
    int insertOperationLog(OperationLog operationLog);

    /**
     * 修改业务操作日志模块
     *
     * @param operationLog 业务操作日志模块
     * @return 结果
     */
    int updateOperationLog(OperationLog operationLog);

    /**
     * 批量删除业务操作日志模块
     *
     * @param ids 需要删除的业务操作日志模块ID
     * @return 结果
     */
    int deleteOperationLogByIds(List<String> ids);

    /**
     * 删除业务操作日志模块信息
     *
     * @param id 业务操作日志模块ID
     * @return 结果
     */
    int deleteOperationLogById(String id);

}

