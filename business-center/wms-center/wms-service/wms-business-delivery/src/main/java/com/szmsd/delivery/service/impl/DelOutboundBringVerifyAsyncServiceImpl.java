package com.szmsd.delivery.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.exception.com.CommonException;
import com.szmsd.delivery.config.AsyncThreadObject;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.enums.DelOutboundStateEnum;
import com.szmsd.delivery.service.IDelOutboundBringVerifyAsyncService;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.service.wrapper.ApplicationContainer;
import com.szmsd.delivery.service.wrapper.ApplicationContext;
import com.szmsd.delivery.service.wrapper.BringVerifyEnum;
import com.szmsd.delivery.service.wrapper.IDelOutboundBringVerifyService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DelOutboundBringVerifyAsyncServiceImpl implements IDelOutboundBringVerifyAsyncService {
    private final Logger logger = LoggerFactory.getLogger(DelOutboundBringVerifyAsyncServiceImpl.class);

    @Autowired
    private IDelOutboundBringVerifyService delOutboundBringVerifyService;
    @Autowired
    private IDelOutboundService delOutboundService;
    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;

    @Override
    public void bringVerifyAsync(String orderNo) {
        String key = applicationName + ":DelOutbound:bringVerifyAsync:" + orderNo;
        RLock lock = this.redissonClient.getLock(key);
        try {
            if (lock.tryLock(0, TimeUnit.SECONDS)) {
                DelOutbound delOutbound = delOutboundService.getByOrderNo(orderNo);
                // 可以提审的状态：待提审，审核失败
                if (DelOutboundStateEnum.REVIEWED.getCode().equals(delOutbound.getState())
                        || DelOutboundStateEnum.AUDIT_FAILED.getCode().equals(delOutbound.getState())) {
                    bringVerifyAsync(delOutbound, AsyncThreadObject.build());
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new CommonException("500", "提审操作失败，" + e.getMessage());
        } finally {
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public void bringVerifyAsync(DelOutbound delOutbound, AsyncThreadObject asyncThreadObject) {
        Thread thread = Thread.currentThread();
        boolean isAsyncThread = !asyncThreadObject.isAsyncThread();
        logger.info("(1)任务开始执行，当前任务名称：{}，当前任务ID：{}，是否为异步任务：{}，任务相关参数：{}", thread.getName(), thread.getId(), isAsyncThread, JSON.toJSONString(asyncThreadObject));
        if (isAsyncThread) {
            asyncThreadObject.loadTid();
        }
        ApplicationContext context = this.delOutboundBringVerifyService.initContext(delOutbound);
        BringVerifyEnum currentState;
        String bringVerifyState = delOutbound.getBringVerifyState();
        if (StringUtils.isEmpty(bringVerifyState)) {
            currentState = BringVerifyEnum.BEGIN;
        } else {
            currentState = BringVerifyEnum.get(bringVerifyState);
            // 兼容
            if (null == currentState) {
                currentState = BringVerifyEnum.BEGIN;
            }
        }
        logger.info("(2)提审异步操作开始，出库单号：{}", delOutbound.getOrderNo());
        ApplicationContainer applicationContainer = new ApplicationContainer(context, currentState, BringVerifyEnum.END, BringVerifyEnum.BEGIN);
        try {
            // 修改状态为提审中
            this.delOutboundService.updateState(delOutbound.getId(), DelOutboundStateEnum.REVIEWED_DOING);
            applicationContainer.action();
            if (DelOutboundConstant.REASSIGN_TYPE_Y.equals(delOutbound.getReassignType())) {
                // 增加出库单已取消记录，异步处理，定时任务
                this.delOutboundCompletedService.add(delOutbound.getOrderNo(), DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
            }
            logger.info("(3)提审异步操作成功，出库单号：{}", delOutbound.getOrderNo());
        } catch (CommonException e) {
            // 回滚操作
            applicationContainer.setEndState(BringVerifyEnum.BEGIN);
            applicationContainer.rollback();
            // 在rollback方法里面已经将BringVerifyState改成Begin了，这里不需要重复去修改状态
            // 更新状态
            // DelOutbound updateDelOutbound = new DelOutbound();
            // updateDelOutbound.setId(delOutbound.getId());
            // updateDelOutbound.setBringVerifyState(BringVerifyEnum.BEGIN.name());
            // this.delOutboundService.updateById(updateDelOutbound);
            // 异步屏蔽异常，将异常打印到日志中
            // 异步错误在单据里面会显示错误信息
            this.logger.error("(4)提审异步操作失败，出库单号：" + delOutbound.getOrderNo() + "，错误原因：" + e.getMessage(), e);
            // 抛出异常
            throw e;
        } finally {
            if (isAsyncThread) {
                asyncThreadObject.unloadTid();
            }
        }
    }

}
