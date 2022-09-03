package com.szmsd.delivery.timer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.delivery.domain.DelOutboundThirdParty;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.enums.DelOutboundConstant;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.service.IDelOutboundThirdPartyService;
import com.szmsd.delivery.util.LockerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 11:45
 */
@Component
public class DelOutboundThirdPartyTimer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    private String applicationName;
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private IDelOutboundThirdPartyService delOutboundThirdPartyService;
    @Autowired
    private DelOutboundThirdPartyAsyncTask delOutboundThirdPartyAsyncTask;
    @Autowired
    private DelOutboundTimerAsyncTaskAdapter delOutboundTimerAsyncTaskAdapter;

    @Value("${thread.trialLimit}")
    private int trialLimit;

    @Value(value = "${server.port:0}")
    private int port;


    /**
     * 单据状态处理中失败的
     * <p/>
     * 5分钟执行一次
     */
    @Async
    public void notifyAmazonLogisticsRouteIdy() {
        String key = applicationName + ":DelOutboundThirdPartyTimer:ThirdParty";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            // 处理失败的单据
            LambdaQueryWrapper<DelOutboundThirdParty> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundThirdParty::getOperationType, DelOutboundConstant.DELOUTBOUND_OPERATION_TYPE_THIRD_PARTY);
            queryWrapper.ne(DelOutboundThirdParty::getState, DelOutboundCompletedStateEnum.SUCCESS.getCode());
            queryWrapper.lt(DelOutboundThirdParty::getHandleSize, 5);
            // 处理时间小于等于当前时间的
            queryWrapper.le(DelOutboundThirdParty::getNextHandleTime, new Date());
            handleThirdParty(queryWrapper);
        });
    }
    @Async
    public void notifyWMS() {
        String key = applicationName + ":DelOutboundThirdPartyTimer:WMS";
        this.doWorker(key, () -> {
            // 查询初始化的任务执行
            // 处理失败的单据
            LambdaQueryWrapper<DelOutboundThirdParty> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundThirdParty::getOperationType, DelOutboundConstant.DELOUTBOUND_OPERATION_TYPE_WMS);
            queryWrapper.ne(DelOutboundThirdParty::getState, DelOutboundCompletedStateEnum.SUCCESS.getCode());
            queryWrapper.lt(DelOutboundThirdParty::getHandleSize, 5);
            // 处理时间小于等于当前时间的
            queryWrapper.le(DelOutboundThirdParty::getNextHandleTime, new Date());
            handleWMS(queryWrapper);
        });
    }



    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

    private void handleThirdParty(LambdaQueryWrapper<DelOutboundThirdParty> queryWrapper) {
        this.handle(queryWrapper, (delOutboundThirdParty, blank) -> this.delOutboundThirdPartyAsyncTask.asyncThirdParty(delOutboundThirdParty), 200, DelOutboundConstant.DELOUTBOUND_OPERATION_TYPE_THIRD_PARTY);
    }

    private void handleWMS(LambdaQueryWrapper<DelOutboundThirdParty> queryWrapper) {
        this.handle(queryWrapper, (delOutboundThirdParty, blank) -> this.delOutboundThirdPartyAsyncTask.asyncWMS(delOutboundThirdParty), 200, DelOutboundConstant.DELOUTBOUND_OPERATION_TYPE_WMS);
    }
    private void fail(Exception e, DelOutboundThirdParty delOutboundThirdParty){
        logger.error(e.getMessage(), e);
        // 处理失败
        // 线程池任务满了，停止执行
        if (e instanceof RejectedExecutionException) {
            logger.error("=============================================");
            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>线程池队列任务溢出");
            logger.error("=============================================");
        }
        this.delOutboundThirdPartyService.fail(delOutboundThirdParty.getId(),
                e.getMessage());
    }
    private void handle(LambdaQueryWrapper<DelOutboundThirdParty> queryWrapper, BiConsumer<DelOutboundThirdParty, Long> consumer, int limit, String type) {
        // 一次处理200
        queryWrapper.last("limit " + limit);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<DelOutboundThirdParty> DelOutboundThirdPartyList = this.delOutboundThirdPartyService.list(queryWrapper);
        stopWatch.stop();
        if (CollectionUtils.isNotEmpty(DelOutboundThirdPartyList)) {
            for (DelOutboundThirdParty delOutboundThirdParty : DelOutboundThirdPartyList) {
                // 增加守护锁，同一条记录如果被多次扫描到，只允许有一个在执行，其它的忽略掉
                String key = applicationName + ":DelOutboundThirdPartyTimer:"+type+":" + delOutboundThirdParty.getId();
                RLock lock = redissonClient.getLock(key);
                try {
                    if (lock.tryLock(0, TimeUnit.SECONDS)) {
                        consumer.accept(delOutboundThirdParty, null);
                    }
                } catch (Exception e) {
                    this.fail(e, delOutboundThirdParty);
                } finally {
                    if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }

            }
        }
    }
}
