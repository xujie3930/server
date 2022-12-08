package com.szmsd.delivery.quartz;

import com.szmsd.delivery.service.DelOutboundEmailService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class DelOutBounderElJob extends QuartzJobBean {
    @Autowired
    private DelOutboundEmailService delOutboundEmailService;
    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext) throws JobExecutionException {


            delOutboundEmailService.selectOmsWmsLog();

    }
}
