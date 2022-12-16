package com.szmsd.finance.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件名: FinanceQuartzConfig.java
 *
 * @author: jiangjun
 * 创建时间: 2022/12/16
 * 描述:
 */

@Configuration
public class FinanceQuartzConfig {


    @Bean
    public JobDetail BasRefundRequestJob() {
        return JobBuilder.newJob(BasRefundRequestJob.class).withIdentity("BasRefundRequestJob").storeDurably().build();
    }



    @Bean
    public Trigger DelOutBounderElJobTrigger() {
        //cron方式，每天上午9点刷0 0 9 * * ?
        return TriggerBuilder.newTrigger().forJob(BasRefundRequestJob())
                .withIdentity("BasRefundRequestJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */5 * * * ?"))
                .build();
    }








}