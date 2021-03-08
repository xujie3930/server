package com.szmsd.open.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.open.domain.OpnRequestLog;

import java.util.List;

/**
 * <p>
 * 请求日志 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-08
 */
public interface IOpnRequestLogService extends IService<OpnRequestLog> {

    /**
     * 查询请求日志模块
     *
     * @param id 请求日志模块ID
     * @return 请求日志模块
     */
    OpnRequestLog selectOpnRequestLogById(String id);

    /**
     * 查询请求日志模块列表
     *
     * @param opnRequestLog 请求日志模块
     * @return 请求日志模块集合
     */
    List<OpnRequestLog> selectOpnRequestLogList(OpnRequestLog opnRequestLog);

    /**
     * 新增请求日志模块
     *
     * @param opnRequestLog 请求日志模块
     * @return 结果
     */
    int insertOpnRequestLog(OpnRequestLog opnRequestLog);

    /**
     * 修改请求日志模块
     *
     * @param opnRequestLog 请求日志模块
     * @return 结果
     */
    int updateOpnRequestLog(OpnRequestLog opnRequestLog);

    /**
     * 批量删除请求日志模块
     *
     * @param ids 需要删除的请求日志模块ID
     * @return 结果
     */
    int deleteOpnRequestLogByIds(List<String> ids);

    /**
     * 删除请求日志模块信息
     *
     * @param id 请求日志模块ID
     * @return 结果
     */
    int deleteOpnRequestLogById(String id);

    /**
     * 新增log
     *
     * @param log log
     * @return int
     */
    int add(OpnRequestLog log);
}

