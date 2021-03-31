package com.szmsd.returnex.runnable;

import com.szmsd.returnex.service.IReturnExpressService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @ClassName: RunnableExecute
 * @Description: 定时任务
 * @Author: 11
 * @Date: 2021/3/31 10:52
 */
@Slf4j
@Component
public class RunnableExecute {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private IReturnExpressService returnExpressService;

    /**
     * 定时任务：超时未处理的预报单；每天24点执行一次
     */
    //@Scheduled(cron = "0 0 0,23 * * *")
    public void executeOperation() {
        log.info("【return_express】超时未处理的预报单 executeOperation() start...");
        RLock lock = redissonClient.getLock("executeOperation");
        try {
            if (lock.tryLock()) {
                returnExpressService.expiredUnprocessedForecastOrder();
            }
        } catch (Exception e) {
            log.error("【return_express】超时未处理的预报单 executeOperation() execute error: ", e);
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
        log.info("【return_express】超时未处理的预报单 executeOperation() end...");
    }
}
