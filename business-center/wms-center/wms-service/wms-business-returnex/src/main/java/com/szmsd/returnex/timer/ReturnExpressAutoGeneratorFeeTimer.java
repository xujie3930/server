package com.szmsd.returnex.timer;

import com.szmsd.returnex.config.LockerUtil;
import com.szmsd.returnex.service.IReturnExpressService;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 退件费自动生成费用
 */
@Component
public class ReturnExpressAutoGeneratorFeeTimer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private RedissonClient redissonClient;
    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private IReturnExpressService iReturnExpressService;

    /**
     * 每1分钟执行一次,自动生成退件费
     */
    @Async
    @Scheduled(cron = "0 */1 * * * ?")
    public void autoGeneratorFee() {
        String key = applicationName + ":ReturnExpressTimer:autoGeneratorFee";
        this.doWorker(key, () -> {
            logger.info("自动生成退件费开始");
            iReturnExpressService.autoGeneratorFee();
            logger.info("自动生成退件费结束");
        });
    }

    /**
     * 每2分钟执行一次,自动生成销毁费
     */
    @Async
    @Scheduled(cron = "0 */2 * * * ?")
    public void autoGeneratorDestoryFee() {
        String key = applicationName + ":ReturnExpressTimer:autoGeneratorDestoryFee";
        this.doWorker(key, () -> {
            logger.info("自动生成销毁费开始");
            iReturnExpressService.autoGeneratorDestoryFee();
            logger.info("自动生成销毁费结束");
        });
    }


    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }
}
