package com.szmsd.common.log.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.szmsd.system.api.feign.RemoteLogService;
import com.szmsd.system.api.domain.SysOperLog;

import javax.annotation.Resource;

/**
 * 异步调用日志服务
 * 
 * @author szmsd
 */
@Service
public class AsyncLogService
{
    @Resource
    private RemoteLogService remoteLogService;

    /**
     * 保存系统日志记录
     */
    @Async
    public void saveSysLog(SysOperLog sysOperLog)
    {
        remoteLogService.saveLog(sysOperLog);
    }
}
