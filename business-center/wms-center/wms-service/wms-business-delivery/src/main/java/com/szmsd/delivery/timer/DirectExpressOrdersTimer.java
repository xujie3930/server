package com.szmsd.delivery.timer;

import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.util.LockerUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Component
public class DirectExpressOrdersTimer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private IDelOutboundService iDelOutboundService;

    /**
     *
     * <p/>
     * 每天12点执行
     */
    @Scheduled(cron = "0 0 12  * * ?")
    @Async
    public void doDirectExpressOrders() {

        String key = applicationName + ":DirectExpressOrders:doOrder";

        this.doWorker(key, () -> {
            logger.info("doDirectExpressOrders 开始执行 ");
            iDelOutboundService.doDirectExpressOrders();
            logger.info("doDirectExpressOrders 开始结束 ");
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

}
