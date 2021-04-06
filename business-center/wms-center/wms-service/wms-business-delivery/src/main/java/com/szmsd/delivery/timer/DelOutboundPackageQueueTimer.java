package com.szmsd.delivery.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 11:45
 */
// @Component
public class DelOutboundPackageQueueTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 每分钟执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0 * * * * ?")
    public void worker() {


    }
}
