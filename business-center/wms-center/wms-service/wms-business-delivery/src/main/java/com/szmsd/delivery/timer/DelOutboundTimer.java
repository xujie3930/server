package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import com.szmsd.delivery.util.LockerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 11:45
 */
@Component
public class DelOutboundTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private IDelOutboundAsyncService delOutboundAsyncService;

    /**
     * 处理完成的单据
     * <p/>
     * 每分钟执行一次
     */
    @Async
    // 秒域 分域 时域 日域 月域 周域 年域
    @Scheduled(cron = "0 * * * * ?")
    public void completed() {
        logger.debug("开始执行任务 - 处理完成的单据");
        String key = applicationName + ":DelOutboundTimer:completed";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.SHIPPED.getCode());
            handleCompleted(queryWrapper);
        });
    }

    /**
     * 处理完成失败的单据
     * <p/>
     * 5分钟执行一次
     */
    @Async
    @Scheduled(cron = "0 */5 * * * ?")
    public void completedFail() {
        logger.debug("开始执行任务 - 处理完成失败的单据");
        String key = applicationName + ":DelOutboundTimer:completedFail";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            // 处理失败的单据
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.FAIL.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.SHIPPED.getCode());
            // 小于3次
            queryWrapper.lt(DelOutboundCompleted::getHandleSize, 3);
            // 处理时间大于等于当前时间的
            queryWrapper.ge(DelOutboundCompleted::getNextHandleTime, new Date());
            handleCompleted(queryWrapper);
        });
    }

    /**
     * 处理取消的单据
     * <p/>
     * 每分钟执行一次
     */
    @Async
    @Scheduled(cron = "0 * * * * ?")
    public void cancelled() {
        logger.debug("开始执行任务 - 处理取消的单据");
        String key = applicationName + ":DelOutboundTimer:cancelled";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.CANCELED.getCode());
            handleCancelled(queryWrapper);
        });
    }

    /**
     * 处理取消失败的单据
     * <p/>
     * 5分钟执行一次
     */
    @Async
    @Scheduled(cron = "0 */5 * * * ?")
    public void cancelledFail() {
        logger.debug("开始执行任务 - 处理取消失败的单据");
        String key = applicationName + ":DelOutboundTimer:cancelledFail";
        this.doWorker(key, () -> {
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.FAIL.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.CANCELED.getCode());
            // 小于3次
            queryWrapper.lt(DelOutboundCompleted::getHandleSize, 3);
            // 处理时间大于等于当前时间的
            queryWrapper.ge(DelOutboundCompleted::getNextHandleTime, new Date());
            this.handleCancelled(queryWrapper);
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

    private void handleCompleted(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper) {
        this.handle(queryWrapper, (orderNo) -> this.delOutboundAsyncService.completed(orderNo));
    }

    public void handleCancelled(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper) {
        this.handle(queryWrapper, orderNo -> this.delOutboundAsyncService.cancelled(orderNo));
    }

    private void handle(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper, Consumer<String> consumer) {
        List<DelOutboundCompleted> delOutboundCompletedList = this.delOutboundCompletedService.list(queryWrapper);
        if (CollectionUtils.isNotEmpty(delOutboundCompletedList)) {
            for (DelOutboundCompleted delOutboundCompleted : delOutboundCompletedList) {
                try {
                    // this.delOutboundService.completed(delOutboundCompleted.getOrderNo());
                    consumer.accept(delOutboundCompleted.getOrderNo());
                    // 处理成功
                    this.delOutboundCompletedService.success(delOutboundCompleted.getId());
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    // 处理失败
                    this.delOutboundCompletedService.fail(delOutboundCompleted.getId());
                }
            }
        }
    }
}
