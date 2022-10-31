package com.szmsd.delivery.timer;

import com.szmsd.delivery.service.IDelOutboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executor;

@Component
public class DirectExpressOrdersTimer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IDelOutboundService iDelOutboundService;

    @Async
    @Scheduled(cron = "0 0 8,14 * * ?")
    public void doDirectExpressOrders() {

        logger.info("doDirectExpressOrders 开始执行 ");

        iDelOutboundService.doDirectExpressOrders();

        logger.info("doDirectExpressOrders 开始结束 ");
    }

}
