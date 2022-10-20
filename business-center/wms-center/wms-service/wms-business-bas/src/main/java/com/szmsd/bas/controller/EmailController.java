package com.szmsd.bas.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.szmsd.bas.domain.BasSeller;
import com.szmsd.bas.dto.EmailDto;
import com.szmsd.bas.enums.EmailEnum;
import com.szmsd.bas.service.IBasEmailService;
import com.szmsd.bas.service.IBasSellerService;
import com.szmsd.bas.util.EmailUtil;
import com.szmsd.common.core.domain.R;
import com.szmsd.common.core.exception.com.AssertUtil;
import com.szmsd.common.core.exception.web.BaseException;
import com.szmsd.common.core.web.controller.BaseController;
import com.szmsd.common.redis.service.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.JobDetailImpl;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Api(tags = {"邮件模块"})
@RestController
@RequestMapping("/bas/email")
public class EmailController extends BaseController {

    @Resource
    private EmailUtil emailUtil;

    @Resource
    private RedisService redisService;

    @Autowired
    private IBasSellerService basSellerService;

    @Resource
    private Executor asyncTaskExecutor;

    @Autowired
    private IBasEmailService iBasEmailService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @PreAuthorize("@ss.hasPermi('bas:email:sendvercode')")
    @GetMapping("/sendVerCode/{email}")
    @ApiOperation(value = "发送验证码", notes = "发送验证码")
    public R sendVerCode(@PathVariable("email") String email) {
        boolean isEmail = EmailUtil.isEmail(email);
        AssertUtil.isTrue(isEmail, "请填写正确的邮箱格式");
        QueryWrapper<BasSeller> queryWrapperEmail = new QueryWrapper<>();
        queryWrapperEmail.eq("init_email",email);
        int count = basSellerService.count(queryWrapperEmail);
        if(count!=0){
           throw new BaseException("Duplicate mailbox，Please change the mailbox");
        }
        EmailEnum varCode = EmailEnum.VAR_CODE;
        String key = varCode.name().concat("-").concat(email);
        String cacheObject = redisService.getCacheObject(key);
        AssertUtil.isTrue(StringUtils.isEmpty(cacheObject), "验证码已发送，不要重复频繁获取");
        // 随机验证码
        String code = RandomStringUtils.random(4, false, true);
        // 设置redis失效时间 30分钟
        redisService.setCacheObject(key, code, varCode.getTimeout(), varCode.getTimeUnit());
        CompletableFuture.runAsync(() -> {
            // 发送
            try {
                emailUtil.sendHtmlMail(email, varCode.getTitle(), varCode.get(code));
            } catch (Exception e) {
                redisService.deleteObject(key);
                log.error("邮件发送失败，请稍后重试, {}, {}", email, e.getMessage(), e);
            }
        }, asyncTaskExecutor);
        return R.ok();
    }

    @PreAuthorize("@ss.hasPermi('bas:email:sendEmail')")
    @PostMapping("/sendEmail")
    @ApiOperation(value = "发送邮件(保存数据库)", notes = "发送邮件(保存数据库)")
    public R sendEmail(@RequestBody EmailDto emailDto) {
//        emailUtil.sendAttachmentMail(emailDto.getTo(), emailDto.getSubject(), emailDto.getText(),emailDto.getFilePath(),emailDto.getList());
           R r=iBasEmailService.sendEmail(emailDto);
        return r;
    }

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
