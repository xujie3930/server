package com.szmsd.pack.quartz;

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
public class QuartzPackageConfig {

    @Bean
    public JobDetail PackageMangJob() {
        return JobBuilder.newJob(PackageMangJob.class).withIdentity("PackageMangJob").storeDurably().build();
    }



    @Bean
    public Trigger PackageMangJobTrigger() {
        //cron方式，每周一凌晨1点刷0 0 1 ? * MON
        //0/3 * * * * ? 3秒测试
        //0 */1 * * * ?  一分钟
        //0 0 */1 * * ? 一小时一次
//        每天23点执行一次：0 0 23 * * ?
//        每天凌晨1点执行一次：0 0 1 * * ?
//        每月1号凌晨1点执行一次：0 0 1 1 * ?
//        每月最后一天23点执行一次：0 0 23 L * ?
//        每周星期天凌晨1点实行一次：0 0 1 ? * L
//        在26分、29分、33分执行一次：0 26,29,33 * * * ?
//        每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
        return TriggerBuilder.newTrigger().forJob(PackageMangJob())
                .withIdentity("PackageMangJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 */1 * * ?"))
                .build();
    }









}