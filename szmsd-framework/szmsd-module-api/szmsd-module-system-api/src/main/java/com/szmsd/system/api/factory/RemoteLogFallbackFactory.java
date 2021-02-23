package com.szmsd.system.api.factory;

import com.szmsd.system.api.feign.RemoteLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.szmsd.common.core.domain.R;
import com.szmsd.system.api.domain.SysOperLog;
import feign.hystrix.FallbackFactory;

/**
 * 日志服务降级处理
 * 
 * @author szmsd
 */
@Component
public class RemoteLogFallbackFactory implements FallbackFactory<RemoteLogService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteLogFallbackFactory.class);

    @Override
    public RemoteLogService create(Throwable throwable)
    {
        log.error("日志服务调用失败:{}", throwable.getMessage());
        return new RemoteLogService()
        {
            @Override
            public R<Boolean> saveLog(SysOperLog sysOperLog)
            {
                log.info("添加日志进熔断返回null");
                return null;
            }
        };
    }
}
