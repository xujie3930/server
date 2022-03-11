package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelSrmCostLog;
import com.szmsd.delivery.enums.DelSrmCostLogEnum;
import com.szmsd.delivery.service.IDelSrmCostLogService;
import com.szmsd.delivery.service.impl.DelSrmCostLogServiceImpl;
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
public class DelSrmCostLogTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelSrmCostLogService delSrmCostLogService;

    /**
     * ty接口请求
     * <p/>
     * 每10s执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0/10 * * * * ?")
    public void srmRequest() {
        // 外层锁，保证定时任务只有一个服务调用
        String key = applicationName + ":DelSrmCostLogTimer:srmRequest";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelSrmCostLog> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.select(DelSrmCostLog::getId, DelSrmCostLog::getOrderNo, DelSrmCostLog::getRequestBody,
                    DelSrmCostLog::getFailCount, DelSrmCostLog::getNextRetryTime, DelSrmCostLog::getType,
                    DelSrmCostLog::getUrl, DelSrmCostLog::getMethod);
            queryWrapper.and(qw -> {
                qw.eq(DelSrmCostLog::getState, DelSrmCostLogEnum.State.WAIT.name())
                        .or()
                        .eq(DelSrmCostLog::getState, DelSrmCostLogEnum.State.FAIL_CONTINUE.name());
            });
            // 小于10次
            queryWrapper.lt(DelSrmCostLog::getFailCount, DelSrmCostLogServiceImpl.retryCount);
            // 处理时间小于等于当前时间的
            queryWrapper.le(DelSrmCostLog::getNextRetryTime, new Date());
            queryWrapper.last("limit 200");
            List<DelSrmCostLog> list = this.delSrmCostLogService.list(queryWrapper);
            if (CollectionUtils.isNotEmpty(list)) {
                for (DelSrmCostLog delSrmCostLog : list) {
                    this.delSrmCostLogService.handler(delSrmCostLog);
                }
            }
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
