package com.szmsd.finance.job;

import com.szmsd.finance.config.ThreadPoolConfig;
import com.szmsd.finance.service.IAccountSerialBillService;
import com.szmsd.inventory.domain.Inventory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class AccountSerialBillNatureJob {

    @Resource
    private IAccountSerialBillService iAccountSerialBillService;

    @Resource
    private RedissonClient redissonClient;

    private Executor asyncTaskExecutor;

    @Resource
    private ThreadPoolConfig threadPoolConfig;

    @PostConstruct
    private void init() {
        asyncTaskExecutor = threadPoolConfig.getAsyncExecutor();
    }

    /**
     * 定时任务：每小时执行一次更新业务账单性质和类型
     */
    @Scheduled(cron = "0 10 0/1 * * ?")
    public void executeSerialBillNature() {

        log.info("executeSerialBillNature() start...");
        RLock lock = redissonClient.getLock("executeSerialBillNature");

        try {
            if (lock.tryLock(3, TimeUnit.SECONDS)) {

                asyncTaskExecutor.execute(() -> {
                    iAccountSerialBillService.executeSerialBillNature();
                });
            }
        } catch (Exception e) {
            log.error("executeSerialBillNature() execute error: ", e);
        } finally {
            if (lock.isLocked()){
                lock.unlock();
            }
        }

        log.info("executeSerialBillNature() end...");

    }
}
