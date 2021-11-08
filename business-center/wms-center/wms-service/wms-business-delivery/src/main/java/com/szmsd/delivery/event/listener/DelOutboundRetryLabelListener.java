package com.szmsd.delivery.event.listener;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundRetryLabel;
import com.szmsd.delivery.enums.DelOutboundRetryLabelStateEnum;
import com.szmsd.delivery.event.DelOutboundRetryLabelEvent;
import com.szmsd.delivery.service.IDelOutboundRetryLabelService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import org.apache.commons.lang3.time.DateUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class DelOutboundRetryLabelListener {
    private final Logger logger = LoggerFactory.getLogger(ShipmentPackingListener.class);

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundRetryLabelService delOutboundRetryLabelService;
    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IDelOutboundService delOutboundService;
    private final int[] retryTimeConfiguration = {30, 30, 60, 60, 180, 180};

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_SHIPMENTENUMLABEL)
    @EventListener
    public void onApplicationEvent(DelOutboundRetryLabelEvent event) {
        Object source = event.getSource();
        Long id = (Long) source;
        String lockName = applicationName + ":DelOutboundRetryLabelEvent:" + id;
        RLock lock = redissonClient.getLock(lockName);
        try {
            if (lock.tryLock(0, 10, TimeUnit.SECONDS)) {
                DelOutboundRetryLabel retryLabel = this.delOutboundRetryLabelService.getById(id);
                if (DelOutboundRetryLabelStateEnum.WAIT.name().equals(retryLabel.getState())
                        || DelOutboundRetryLabelStateEnum.FAIL_CONTINUE.name().equals(retryLabel.getState())) {
                    DelOutbound delOutbound = delOutboundService.getByOrderNo(retryLabel.getOrderNo());
                    String lastFailMessage = "";
                    int failCount = retryLabel.getFailCount();
                    String state;
                    long st = System.currentTimeMillis();
                    Date nextRetryTime = null;
                    try {
                        // 获取标签
                        delOutboundBringVerifyService.getShipmentLabel(delOutbound);
                        // 推送标签
                        this.delOutboundBringVerifyService.htpShipmentLabel(delOutbound);
                        // 发货指令
                        this.delOutboundBringVerifyService.shipmentShipping(delOutbound);
                        state = DelOutboundRetryLabelStateEnum.SUCCESS.name();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        lastFailMessage = e.getMessage();
                        if (lastFailMessage.length() > 500)
                            lastFailMessage = lastFailMessage.substring(0, 500);
                        failCount++;
                        if (failCount > 5) {
                            state = DelOutboundRetryLabelStateEnum.FAIL.name();
                        } else {
                            state = DelOutboundRetryLabelStateEnum.FAIL_CONTINUE.name();
                            int t = retryTimeConfiguration[failCount];
                            nextRetryTime = DateUtils.addMinutes(retryLabel.getNextRetryTime(), t);
                        }
                    }
                    int lastRequestConsumeTime = (int) (System.currentTimeMillis() - st);
                    // retry time
                    // 9m
                    // 30s, 30s, 60s, 60s, 180s, 180s
                    LambdaUpdateWrapper<DelOutboundRetryLabel> updateWrapper = Wrappers.lambdaUpdate();
                    updateWrapper.set(DelOutboundRetryLabel::getState, state);
                    updateWrapper.set(DelOutboundRetryLabel::getFailCount, failCount);
                    updateWrapper.set(DelOutboundRetryLabel::getLastFailMessage, lastFailMessage);
                    updateWrapper.set(DelOutboundRetryLabel::getLastRequestConsumeTime, lastRequestConsumeTime);
                    updateWrapper.set(DelOutboundRetryLabel::getNextRetryTime, nextRetryTime);
                    updateWrapper.eq(DelOutboundRetryLabel::getId, retryLabel.getId());
                    this.delOutboundRetryLabelService.update(updateWrapper);
                }
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
}
