package com.szmsd.delivery.quartz;

import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 文件名: QuartzConfig.java
 *
 * @author: jiangjun
 * 创建时间: 2022/8/12
 * 描述:
 */

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail DelOutboundJob() {
        return JobBuilder.newJob(DelOutboundJob.class).withIdentity("DelOutboundJob").storeDurably().build();
    }

    @Bean
    public Trigger DelOutboundJobTrigger() {
        //cron方式，每天0点刷
        return TriggerBuilder.newTrigger().forJob(DelOutboundJob())
                .withIdentity("DelOutboundJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ?"))
                .build();
    }




}