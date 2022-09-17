package com.szmsd.returnex.quartz;

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
public class QuartzReturnexConfig {

    @Bean
    public JobDetail BasRetreatPieceJob() {
        return JobBuilder.newJob(BasRetreatPieceJob.class).withIdentity("BasRetreatPieceJob").storeDurably().build();
    }


    @Bean
    public Trigger BasRetreatPieceJobTrigger() {
        //cron方式，每周一凌晨1点刷0 0 1 ? * MON
        //0/3 * * * * ? 3秒测试
        //0 0 1 * * ? 每天凌晨一点
        return TriggerBuilder.newTrigger().forJob(BasRetreatPieceJob())
                .withIdentity("BasRetreatPieceJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 * * ?"))
                .build();
    }








}