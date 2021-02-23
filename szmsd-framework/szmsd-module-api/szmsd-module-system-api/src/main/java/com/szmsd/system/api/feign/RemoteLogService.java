package com.szmsd.system.api.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.szmsd.common.core.constant.ServiceNameConstants;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.domain.SysOperLog;
import com.szmsd.system.api.factory.RemoteLogFallbackFactory;

/**
 * 日志服务
 * 
 * @author szmsd
 */
@FeignClient(contextId = "remoteLogService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteLogFallbackFactory.class)
public interface RemoteLogService
{
    /**
     * 保存系统日志
     *
     * @param sysOperLog 日志实体
     * @param from 内部调用标志
     * @return 结果
     */
    @PostMapping("/operlog/add")
    R<Boolean> saveLog(@RequestBody SysOperLog sysOperLog);
}
