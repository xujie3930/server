package com.szmsd.http.quartz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.quartz.*;

/**
 * 文件名: QuartzConfig.java
 *
 * @author: jiangjun
 * 创建时间: 2022/8/12
 * 描述:
 */

@Configuration
public class QuartzHttpTjConfig {

    @Bean
    public JobDetail TjJob() {
        return JobBuilder.newJob(TjJob.class).withIdentity("TjJob").storeDurably().build();
    }

    @Bean
    public Trigger DelOutboundJobTrigger() {
        //cron方式，每周一凌晨1点刷0 0 1 ? * MON
        //0/3 * * * * ? 3秒测试
        //0 */1 * * * ?  一分钟
//        每天23点执行一次：0 0 23 * * ?
//        每天凌晨1点执行一次：0 0 1 * * ?
//        每月1号凌晨1点执行一次：0 0 1 1 * ?
//        每月最后一天23点执行一次：0 0 23 L * ?
//        每周星期天凌晨1点实行一次：0 0 1 ? * L
//        在26分、29分、33分执行一次：0 26,29,33 * * * ?
//        每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
        return TriggerBuilder.newTrigger().forJob(TjJob())
                .withIdentity("TjJob")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 1 ? * MON"))
                .build();
    }






}