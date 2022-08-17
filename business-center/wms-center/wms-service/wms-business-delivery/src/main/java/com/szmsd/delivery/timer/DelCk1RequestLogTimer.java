package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelCk1RequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.service.IDelCk1RequestLogService;
import com.szmsd.delivery.service.impl.DelCk1RequestLogServiceImpl;
import com.szmsd.delivery.util.LockerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class DelCk1RequestLogTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelCk1RequestLogService delCk1RequestLogService;

    /**
     * ck1接口请求
     * <p/>
     * 每10s执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    //@Scheduled(cron = "0/10 * * * * ?")
    public void ck1Request() {
        // 外层锁，保证定时任务只有一个服务调用
        String key = applicationName + ":DelCk1RequestLogTimer:ck1Request";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelCk1RequestLog> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.select(DelCk1RequestLog::getId, DelCk1RequestLog::getOrderNo, DelCk1RequestLog::getRequestBody,
                    DelCk1RequestLog::getFailCount, DelCk1RequestLog::getNextRetryTime, DelCk1RequestLog::getType,
                    DelCk1RequestLog::getUrl, DelCk1RequestLog::getRemark);
            queryWrapper.and(qw -> {
                qw.eq(DelCk1RequestLog::getState, DelCk1RequestLogConstant.State.WAIT.name())
                        .or()
                        .eq(DelCk1RequestLog::getState, DelCk1RequestLogConstant.State.FAIL_CONTINUE.name());
            });
            // 小于10次
            queryWrapper.lt(DelCk1RequestLog::getFailCount, DelCk1RequestLogServiceImpl.retryCount);
            // 处理时间小于等于当前时间的
            queryWrapper.le(DelCk1RequestLog::getNextRetryTime, new Date());
            queryWrapper.last("limit 200");
            List<DelCk1RequestLog> list = this.delCk1RequestLogService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                for (DelCk1RequestLog ck1RequestLog : list) {
                    this.delCk1RequestLogService.handler(ck1RequestLog);
                }
            }
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
