package com.szmsd.delivery.timer;

import com.szmsd.delivery.service.OfflineDeliveryService;
import com.szmsd.delivery.util.LockerUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OfflineDeliveryTimer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OfflineDeliveryService offlineDeliveryService;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 线下出库异步处理
     * <p/>
     * 每30s执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0/30 * * * * ?")
    public void autoOfflineDelivery() {
        logger.info("OfflineDeliveryTimer  线下出库开始========");
        String key = "OfflineDeliveryTimer:autoOfflineDelivery";
        this.doWorker(key, () -> {
            offlineDeliveryService.dealOfflineDelivery();
        });
        logger.info("OfflineDeliveryTimer  线下出库结束========");
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

}
