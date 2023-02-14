package com.szmsd.finance.job;

import com.szmsd.finance.service.FssAccountBalanceLogNewService;
import com.szmsd.finance.service.IAccountBalanceLogService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AccountBalanceAutoGeneratorJob {


    @Autowired
    private IAccountBalanceLogService iAccountBalanceLogService;

    @Autowired
    private FssAccountBalanceLogNewService fssAccountBalanceLogNewService;

    @Resource
    private RedissonClient redissonClient;

    /**
     * 定时任务：每天定时备份账户余额表,晚上12点之前
     */
    @Scheduled(cron = "40 59 23 * * ?")
    public void autoGeneratorBalance() {
        log.info("autoGeneratorBalance() start...");
        RLock lock = redissonClient.getLock("autoGeneratorBalance");

        try {
            if (lock.tryLock(3, TimeUnit.SECONDS)) {
                iAccountBalanceLogService.autoGeneratorBalance();
            }
        } catch (Exception e) {
            log.error("autoGeneratorBalance() execute error: ", e);
        } finally {
            if (lock.isLocked()){
                lock.unlock();
            }
        }

        log.info("autoGeneratorBalance() end...");
    }


    /**
     * 定时任务：每天0点定时备份账户余额表
     */
    @Scheduled(cron = "0 0 0 * * ?")
//    @Scheduled(cron = "* */1 * * * ?")
    public void autoSyncBalance() {
        log.info("autoSyncBalance() start...");

        try {
            fssAccountBalanceLogNewService.autoSyncBalance();
        } catch (Exception e) {
            log.error("autoSyncBalance() execute error: ", e);
        }
        log.info("autoSyncBalance() end...");
    }


}
