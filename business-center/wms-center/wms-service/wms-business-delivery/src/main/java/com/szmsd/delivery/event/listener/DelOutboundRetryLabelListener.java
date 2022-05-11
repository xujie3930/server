package com.szmsd.delivery.event.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundRetryLabel;
import com.szmsd.delivery.enums.DelOutboundRetryLabelStateEnum;
import com.szmsd.delivery.enums.DelOutboundTrackingAcquireTypeEnum;
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
    //                                            0   1   2   3   4   5   6   7   8    9    10   11
    private final int[] retryTimeConfiguration = {30, 30, 60, 60, 60, 60, 60, 60, 180, 180, 180, 180};
    public static final int retryCount = 10;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_SHIPMENTENUMLABEL)
    @EventListener
    public void onApplicationEvent(DelOutboundRetryLabelEvent event) {
        Object source = event.getSource();
        boolean force = event.isForce();
        Long id = (Long) source;
        String lockName = applicationName + ":DelOutboundRetryLabelEvent:" + id;
        logger.info("(1)获取承运商标签，id：{}，lockName：{}", id, lockName);
        RLock lock = redissonClient.getLock(lockName);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                logger.info("(2)获取到锁，id：{}，lockName：{}", id, lockName);
                DelOutboundRetryLabel retryLabel = this.delOutboundRetryLabelService.getById(id);
                if (null == retryLabel) {
                    logger.info("(4)查询记录失败，id：{}", id);
                    return;
                }
                // 只推送标签
                boolean pushLabel = "pushLabel".equals(retryLabel.getRemark());
                logger.info("(5)查询记录成功，id：{}，state：{}，对象JSON：{}", id, retryLabel.getState(), JSON.toJSONString(retryLabel));
                if ((DelOutboundRetryLabelStateEnum.WAIT.name().equals(retryLabel.getState())
                        || DelOutboundRetryLabelStateEnum.FAIL_CONTINUE.name().equals(retryLabel.getState())) || force) {
                    DelOutbound delOutbound = delOutboundService.getByOrderNo(retryLabel.getOrderNo());
                    String lastFailMessage = "";
                    int failCount = retryLabel.getFailCount();
                    String state;
                    long st = System.currentTimeMillis();
                    Date nextRetryTime = null;
                    try {
                        if (null != delOutbound) {
                            // 获取标签
                            // 只要是以下两种就需要去获取标签：
                            // 1.下单后供应商获取
                            // 2.核重后供应商获取
                            if (!DelOutboundTrackingAcquireTypeEnum.NONE.getCode().equals(delOutbound.getTrackingAcquireType())) {
                                delOutboundBringVerifyService.getShipmentLabel(delOutbound);
                                logger.info("(7)获取标签完成，id：{}", id);
                            }
                            // 推送标签，处理那种存在自己上传文件的逻辑，自己上传的文件也需要推送给WMS
                            this.delOutboundBringVerifyService.htpShipmentLabel(delOutbound);
                            logger.info("(8)推送标签完成，id：{}", id);
                            // 发货指令
                            if (!pushLabel) {
                                this.delOutboundBringVerifyService.shipmentShipping(delOutbound);
                                logger.info("(9)调用成功发货指令完成，id：{}", id);
                            }
                            // 调用忽略异常信息的接口，异步处理
                            this.delOutboundBringVerifyService.ignoreExceptionInfo(delOutbound.getOrderNo());
                            state = DelOutboundRetryLabelStateEnum.SUCCESS.name();
                        } else {
                            throw new CommonException("999", "出库单：" + retryLabel.getOrderNo() + "，不存在");
                        }
                    } catch (Exception e) {
                        logger.info("(8)获取标签失败，id：{}，错误信息：{}", id, e.getMessage());
                        logger.error(e.getMessage(), e);
                        lastFailMessage = e.getMessage();
                        if (null == lastFailMessage) {
                            lastFailMessage = "获取标签失败";
                        }
                        if (lastFailMessage.length() > 500)
                            lastFailMessage = lastFailMessage.substring(0, 500);
                        failCount++;
                        if (failCount >= retryCount) {
                            state = DelOutboundRetryLabelStateEnum.FAIL.name();
                            // 发货指令
                            if (!pushLabel) {
                                this.delOutboundBringVerifyService.shipmentShippingEx(delOutbound, lastFailMessage);
                                logger.info("(10)调用失败发货指令完成，id：{}", id);
                            }
                        } else {
                            state = DelOutboundRetryLabelStateEnum.FAIL_CONTINUE.name();
                            int t = retryTimeConfiguration[failCount];
                            nextRetryTime = DateUtils.addSeconds(retryLabel.getNextRetryTime(), t);
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
                } else {
                    logger.info("(6)状态不正确不处理，id：{}", id);
                }
            } else {
                logger.info("(3)没有获取到锁，id：{}，lockName：{}", id, lockName);
            }
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
