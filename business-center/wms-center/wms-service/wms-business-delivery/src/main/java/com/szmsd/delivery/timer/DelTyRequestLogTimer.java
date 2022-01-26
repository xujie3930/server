package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelTyRequestLog;
import com.szmsd.delivery.enums.DelTyRequestLogConstant;
import com.szmsd.delivery.service.IDelTyRequestLogService;
import com.szmsd.delivery.service.impl.DelTyRequestLogServiceImpl;
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
public class DelTyRequestLogTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelTyRequestLogService delTyRequestLogService;

    /**
     * ty接口请求
     * <p/>
     * 每10s执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0/10 * * * * ?")
    public void tyRequest() {
        // 外层锁，保证定时任务只有一个服务调用
        String key = applicationName + ":DelTyRequestLogTimer:tyRequest";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelTyRequestLog> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.select(DelTyRequestLog::getId, DelTyRequestLog::getOrderNo, DelTyRequestLog::getRequestBody,
                    DelTyRequestLog::getFailCount, DelTyRequestLog::getNextRetryTime, DelTyRequestLog::getType,
                    DelTyRequestLog::getUrl, DelTyRequestLog::getMethod);
            queryWrapper.and(qw -> {
                qw.eq(DelTyRequestLog::getState, DelTyRequestLogConstant.State.WAIT.name())
                        .or()
                        .eq(DelTyRequestLog::getState, DelTyRequestLogConstant.State.FAIL_CONTINUE.name());
            });
            // 小于10次
            queryWrapper.lt(DelTyRequestLog::getFailCount, DelTyRequestLogServiceImpl.retryCount);
            // 处理时间小于等于当前时间的
            queryWrapper.le(DelTyRequestLog::getNextRetryTime, new Date());
            queryWrapper.last("limit 200");
            List<DelTyRequestLog> list = this.delTyRequestLogService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                for (DelTyRequestLog tyRequestLog : list) {
                    this.delTyRequestLogService.handler(tyRequestLog);
                }
            }
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
