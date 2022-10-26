package com.szmsd.delivery.timer;

import com.szmsd.delivery.service.IDelOutboundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class DirectExpressOrdersTimer {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IDelOutboundService iDelOutboundService;


    @Scheduled(cron = "0 0 23 * * ?")
    public void doDirectExpressOrders() {
        iDelOutboundService.doDirectExpressOrders();
    }

}
