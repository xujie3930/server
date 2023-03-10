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
    public JobDetail DelOutBounderElJob() {
        return JobBuilder.newJob(DelOutBounderElJob.class).withIdentity("DelOutBounderElJob").storeDurably().build();
    }

    @Bean
    public JobDetail DelOutboundJob() {
        return JobBuilder.newJob(DelOutboundJob.class).withIdentity("DelOutboundJob").storeDurably().build();
    }

    @Bean
    public JobDetail DelQueryServiceJob() {
        return JobBuilder.newJob(DelQueryServiceJob.class).withIdentity("DelQueryServiceJob").storeDurably().build();
    }

    @Bean
    public JobDetail DeleTrackingPushJob() {
        return JobBuilder.newJob(DeleTrackingPushJob.class).withIdentity("DeleTrackingPushJob").storeDurably().build();
    }


    @Bean
    public Trigger DeleTrackingPushJobTrigger() {
        //cron方式，每周一凌晨1点刷0 0 1 ? * MON
        //0 0 */1 * * ? 一个小时
        return TriggerBuilder.newTrigger().forJob(DeleTrackingPushJob())
                .withIdentity("DeleTrackingPushJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 */30 * * * ?"))
                .build();
    }


    @Bean
    public Trigger DelOutboundJobTrigger() {
        //cron方式，每周一凌晨1点刷0 0 1 ? * MON
        //0/3 * * * * ? 3秒测试
        return TriggerBuilder.newTrigger().forJob(DelOutboundJob())
                .withIdentity("DelOutboundJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * MON"))
                .build();
    }
    @Bean
    public Trigger DelOutBounderElJobTrigger() {
        //cron方式，每天上午9点刷0 0 9 * * ?
        return TriggerBuilder.newTrigger().forJob(DelOutBounderElJob())
                .withIdentity("DelOutBounderElJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 9 * * ?"))
                .build();
    }


    @Bean
    public Trigger DelQueryServiceJobTrigger() {
        //cron方式，每天晚上凌晨1点刷0 0 1 * * ?
        //0 */1 * * * ?  一分钟

        return TriggerBuilder.newTrigger().forJob(DelQueryServiceJob())
                .withIdentity("DelQueryServiceJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
                .build();
    }





}