package com.szmsd.http.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.szmsd.http.domain.HtpRequestLog;

import java.util.List;

/**
 * <p>
 * http请求日志 服务类
 * </p>
 *
 * @author asd
 * @since 2021-03-09
 */
public interface IHtpRequestLogService extends IService<HtpRequestLog> {

    /**
     * 查询http请求日志模块
     *
     * @param id http请求日志模块ID
     * @return http请求日志模块
     */
    HtpRequestLog selectHtpRequestLogById(String id);

    /**
     * 查询http请求日志模块列表
     *
     * @param htpRequestLog http请求日志模块
     * @return http请求日志模块集合
     */
    List<HtpRequestLog> selectHtpRequestLogList(HtpRequestLog htpRequestLog);

    /**
     * 新增http请求日志模块
     *
     * @param htpRequestLog http请求日志模块
     * @return 结果
     */
    int insertHtpRequestLog(HtpRequestLog htpRequestLog);

    /**
     * 修改http请求日志模块
     *
     * @param htpRequestLog http请求日志模块
     * @return 结果
     */
    int updateHtpRequestLog(HtpRequestLog htpRequestLog);

    /**
     * 批量删除http请求日志模块
     *
     * @param ids 需要删除的http请求日志模块ID
     * @return 结果
     */
    int deleteHtpRequestLogByIds(List<String> ids);

    /**
     * 删除http请求日志模块信息
     *
     * @param id http请求日志模块ID
     * @return 结果
     */
    int deleteHtpRequestLogById(String id);

    /**
     * add
     *
     * @param log log
     * @return int
     */
    int add(HtpRequestLog log);
}

