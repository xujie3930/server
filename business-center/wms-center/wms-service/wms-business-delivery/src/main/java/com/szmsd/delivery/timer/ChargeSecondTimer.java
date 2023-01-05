package com.szmsd.delivery.timer;

import com.szmsd.delivery.service.ChargeService;
import com.szmsd.delivery.util.LockerUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ChargeSecondTimer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ChargeService chargeService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 二次计费定时器
     * <p/>
     * 每1分执行一次
     */
    @Async
    @Scheduled(cron = "0 */2 * * * ?")
    public void doSecondCharge() {
        logger.info("ChargeSecondTimer  二次计费开始========");
        String key = "ChargeSecondTimer:doSecondCharge";
        this.doWorker(key, () -> {
            chargeService.doSecondCharge();
        });
        logger.info("ChargeSecondTimer  二次计费结束========");
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

}
