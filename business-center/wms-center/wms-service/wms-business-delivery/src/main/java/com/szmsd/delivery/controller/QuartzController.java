package com.szmsd.delivery.controller;

import cn.hutool.core.date.DateUtil;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 定时任务获取下次执行时间控制层
 */
@RestController
@RequestMapping("/quartz")
public class QuartzController {
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    /**
     * 获取所有定时任务
     *
     * @return
     */
    @GetMapping("/getJobList")
    public List<Map<String, String>> getJobList() {
        List<Map<String, String>> jobList = new ArrayList<>();
        try {
            //获取Scheduler
            Scheduler scheduler = schedulerFactoryBean.getScheduler();
            //再获取Scheduler下的所有group
            List<String> triggerGroupNames = scheduler.getTriggerGroupNames();
            for (String groupName : triggerGroupNames) {
                //组装group的匹配，为了模糊获取所有的triggerKey或者jobKey
                GroupMatcher groupMatcher = GroupMatcher.groupEquals(groupName);
                //获取所有的triggerKey
                Set<TriggerKey> triggerKeySet = scheduler.getTriggerKeys(groupMatcher);
                for (TriggerKey triggerKey : triggerKeySet) {
                    //通过triggerKey在scheduler中获取trigger对象
                    CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
                    //获取trigger拥有的Job
                    JobKey jobKey = trigger.getJobKey();
                    JobDetailImpl jobDetail = (JobDetailImpl) scheduler.getJobDetail(jobKey);
                    //组装需要显示的数据
                    Map<String, String> jobMap = new HashMap<>();
                    //分组名称
                    jobMap.put("groupName", groupName);
                    //定时任务名称
                    jobMap.put("jobDetailName", jobDetail.getName());
                    //cron表达式
                    String cronExpression = trigger.getCronExpression();
                    jobMap.put("jobCronExpression", cronExpression);
                    //时区
                    jobMap.put("timeZone", trigger.getTimeZone().getID());
                    //下次运行时间
                    CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
                    cronTriggerImpl.setCronExpression(cronExpression);
                    List<Date> dates = TriggerUtils.computeFireTimes(cronTriggerImpl, null, 20);
                    jobMap.put("nextRunDateTime", DateUtil.format(dates.get(0), "yyyy-MM-dd HH:mm:ss"));
                    jobList.add(jobMap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jobList;
    }


}
