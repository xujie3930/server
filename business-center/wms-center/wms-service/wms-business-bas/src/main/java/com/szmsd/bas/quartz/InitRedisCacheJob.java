package com.szmsd.bas.quartz;


import com.szmsd.bas.service.InitRedisCacheService;
import com.szmsd.common.core.utils.SpringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class InitRedisCacheJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        InitRedisCacheService initRedisCacheService= SpringUtils.getBean(InitRedisCacheService.class);
        initRedisCacheService.updateWarehouseRedis();
    }
}
