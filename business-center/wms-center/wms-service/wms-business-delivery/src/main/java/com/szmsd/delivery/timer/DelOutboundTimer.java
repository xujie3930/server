package com.szmsd.delivery.timer;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.szmsd.bas.domain.BasDeliveryServiceMatching;
import com.szmsd.chargerules.domain.ChargeLog;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelOutbound;
import com.szmsd.delivery.domain.DelOutboundCompleted;
import com.szmsd.delivery.enums.DelOutboundCompletedStateEnum;
import com.szmsd.delivery.enums.DelOutboundOperationTypeEnum;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.IDelOutboundService;
import com.szmsd.delivery.util.LockerUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

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
    private DelOutboundTimerAsyncTask delOutboundTimerAsyncTask;
    @Autowired
    private DelOutboundTimerAsyncTaskAdapter delOutboundTimerAsyncTaskAdapter;

    @Autowired
    private IDelOutboundService delOutboundService;

    @Value("${thread.trialLimit}")
    private int trialLimit;

    @Value(value = "${server.port:0}")
    private int port;

    /**
     * ?????????????????????
     * <p/>
     * ?????????????????????
     */
    @Async
    // ?????? ?????? ?????? ?????? ?????? ?????? ??????
    @Scheduled(cron = "0 * * * * ?")
    public void processing() {
        String key = applicationName + ":DelOutboundTimer:processing";
        this.doWorker(key, () -> {
            // ??????????????????????????????
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.PROCESSING.getCode());
            handleProcessing(queryWrapper);
        });
    }

    /**
     * ??????????????????????????????
     * <p/>
     * 5??????????????????
     */
    @Async
    @Scheduled(cron = "0 */5 * * * ?")
    public void processingFail() {
        String key = applicationName + ":DelOutboundTimer:processingFail";
        this.doWorker(key, () -> {
            // ??????????????????????????????
            // ?????????????????????
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.FAIL.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.PROCESSING.getCode());
            queryWrapper.lt(DelOutboundCompleted::getHandleSize, 5);
            // ???????????????????????????????????????
            queryWrapper.le(DelOutboundCompleted::getNextHandleTime, new Date());
            handleProcessing(queryWrapper);
        });
    }

    /**
     * ?????????????????????
     * <p/>
     * ?????????????????????
     */
    @Async
    // ?????? ?????? ?????? ?????? ?????? ?????? ??????
    @Scheduled(cron = "0 * * * * ?")
    public void completed() {
        logger.debug("?????????????????? - ?????????????????????");
        String key = applicationName + ":DelOutboundTimer:completed";
        this.doWorker(key, () -> {
            // ??????????????????????????????
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.SHIPPED.getCode());
            queryWrapper.and(x -> x.ne(DelOutboundCompleted::getCreateByName, "pushDate").
                    or().le(DelOutboundCompleted::getNextHandleTime, new Date())
            );
            handleCompleted(queryWrapper);
        });
    }

    /**
     * ???????????????????????????
     * <p/>
     * 3??????????????????
     */
    @Async
    @Scheduled(cron = "0 */3 * * * ?")
    public void completedFail() {
        logger.debug("?????????????????? - ???????????????????????????");
        String key = applicationName + ":DelOutboundTimer:completedFail";
        this.doWorker(key, () -> {
            // ??????????????????????????????
            // ?????????????????????
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.FAIL.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.SHIPPED.getCode());
            queryWrapper.lt(DelOutboundCompleted::getHandleSize, 10);
            // ???????????????????????????????????????
            queryWrapper.le(DelOutboundCompleted::getNextHandleTime, new Date());
            handleCompleted(queryWrapper);
        });
    }

    /**
     * ?????????????????????
     * <p/>
     * ?????????????????????
     */
    @Async
    @Scheduled(cron = "0 * * * * ?")
    public void cancelled() {
        logger.debug("?????????????????? - ?????????????????????");
        String key = applicationName + ":DelOutboundTimer:cancelled";
        this.doWorker(key, () -> {
            // ??????????????????????????????
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.CANCELED.getCode());
            handleCancelled(queryWrapper);
        });
    }

    /**
     * ???????????????????????????
     * <p/>
     * 5??????????????????
     */
    @Async
    @Scheduled(cron = "0 */5 * * * ?")
    public void cancelledFail() {
        logger.debug("?????????????????? - ???????????????????????????");
        String key = applicationName + ":DelOutboundTimer:cancelledFail";
        this.doWorker(key, () -> {
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.FAIL.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.CANCELED.getCode());
            queryWrapper.lt(DelOutboundCompleted::getHandleSize, 5);
            // ???????????????????????????????????????
            queryWrapper.le(DelOutboundCompleted::getNextHandleTime, new Date());
            this.handleCancelled(queryWrapper);
        });
    }

    @Resource(name = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_DELOUTBOUND_REVIEWED)
    private ThreadPoolExecutor bringVerifyThreadExecutor;

    /**
     * ?????????????????????
     *
     *  job ??????controller  notifyBringVerify
     *
     */
    @Async
    public void bringVerify() {
        logger.info("[port:{}][{}][???????????????]bringVerify ???????????? ????????????", port, Thread.currentThread().getId());
        try {
            logger.info("[port:{}][{}][???????????????]bringVerify ???????????? ??????????????????:{},?????????:{},????????????:{},????????????:{}", port, Thread.currentThread().getId(), bringVerifyThreadExecutor.getQueue().size(), bringVerifyThreadExecutor.getActiveCount(), bringVerifyThreadExecutor.getTaskCount(), bringVerifyThreadExecutor.getCorePoolSize());
        } catch (Exception e) {
            logger.info("[port:{}][{}][???????????????]bringVerify ???????????? ????????????????????????????????????", port, Thread.currentThread().getId(),e);
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        String key = applicationName + ":DelOutboundTimer:bringVerify";
        this.doWorker(key, () -> {
            // ??????????????????????????????
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
            handleBringVerify(queryWrapper);
        });

    }

    /**
     * 5??????????????????
     */
    @Async
    @Scheduled(cron = "0/10 * * * * ?")
    public void bringVerifyFail() {
        logger.debug("?????????????????? - ??????");
        String key = applicationName + ":DelOutboundTimer:bringVerifyFail";
        this.doWorker(key, () -> {
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.FAIL.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.BRING_VERIFY.getCode());
            queryWrapper.lt(DelOutboundCompleted::getHandleSize, 5);
            // ???????????????????????????????????????
            queryWrapper.le(DelOutboundCompleted::getNextHandleTime, new Date());
            this.handleBringVerify(queryWrapper);
        });
    }

    /**
     * ?????????????????????
     */
    @Async
    @Scheduled(cron = "0 * * * * ?")
    public void shipmentPacking() {
        logger.debug("?????????????????? - ??????");
        String key = applicationName + ":DelOutboundTimer:shipmentPacking";
        this.doWorker(key, () -> {
            // ??????????????????????????????
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.INIT.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
            handleShipmentPacking(queryWrapper);
        });
    }

    /**
     * 5??????????????????
     */
    @Async
    @Scheduled(cron = "0 */5 * * * ?")
    public void shipmentPackingFail() {
        logger.debug("?????????????????? - ??????");
        String key = applicationName + ":DelOutboundTimer:shipmentPackingFail";
        this.doWorker(key, () -> {
            LambdaQueryWrapper<DelOutboundCompleted> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(DelOutboundCompleted::getState, DelOutboundCompletedStateEnum.FAIL.getCode());
            queryWrapper.eq(DelOutboundCompleted::getOperationType, DelOutboundOperationTypeEnum.SHIPMENT_PACKING.getCode());
            queryWrapper.lt(DelOutboundCompleted::getHandleSize, 5);
            // ???????????????????????????????????????
            queryWrapper.le(DelOutboundCompleted::getNextHandleTime, new Date());
            this.handleShipmentPacking(queryWrapper);
        });
    }

    private void doWorker(String key, LockerUtil.Worker worker) {
        new LockerUtil<Integer>(redissonClient).tryLock(key, worker);
    }

    private void handleProcessing(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper) {
        this.handle(queryWrapper, (orderNo, id) -> this.delOutboundTimerAsyncTask.asyncHandleProcessing(orderNo, id), 200, true);
    }

    private void handleCompleted(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper) {
        this.handlePush(queryWrapper, (orderNo, completed) -> this.delOutboundTimerAsyncTask.asyncHandleCompleted(completed), 200, true);
    }

    public void handleCancelled(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper) {
        this.handle(queryWrapper, (orderNo, id) -> this.delOutboundTimerAsyncTask.asyncHandleCancelled(orderNo, id), 200, true);
    }

    public void handleBringVerify(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper) {
        this.handle(queryWrapper, (orderNo, id) -> this.delOutboundTimerAsyncTaskAdapter.asyncBringVerify(orderNo, id), trialLimit);
    }

    public void handleShipmentPacking(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper) {
        this.handle(queryWrapper, (orderNo, id) -> this.delOutboundTimerAsyncTask.asyncShipmentPacking(orderNo, id), 350, true);
    }

    private void handle(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper, BiConsumer<String, Long> consumer, int limit) {
        // ??????100
        this.handle(queryWrapper, consumer, limit, false);
    }

    private void handlePush(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper, BiConsumer<String, DelOutboundCompleted> consumer, int limit, boolean needLock) {
        // ????????????200
        queryWrapper.last("limit " + limit);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<DelOutboundCompleted> delOutboundCompletedList = this.delOutboundCompletedService.list(queryWrapper);
        stopWatch.stop();
        logger.info(">>>>>[???????????????]??????????????????????????????"+stopWatch.getLastTaskTimeMillis());
        if (CollectionUtils.isNotEmpty(delOutboundCompletedList)) {
            for (DelOutboundCompleted delOutboundCompleted : delOutboundCompletedList) {
                if (needLock) {
                    // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    String key = applicationName + ":DelOutboundTimer:handle:" + delOutboundCompleted.getId();
                    RLock lock = redissonClient.getLock(key);
                    try {
                        if (lock.tryLock(0, TimeUnit.SECONDS)) {
                            consumer.accept(delOutboundCompleted.getOrderNo(), delOutboundCompleted);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        // ????????????
                        this.delOutboundCompletedService.fail(delOutboundCompleted.getId(), e.getMessage());
                        // ????????????????????????????????????
                        if (e instanceof RejectedExecutionException) {
                            logger.error("=============================================");
                            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>???????????????????????????");
                            logger.error("=============================================");
                            break;
                        }
                    } finally {
                        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                            lock.unlock();
                        }
                    }
                } else {
                    try {
                        consumer.accept(delOutboundCompleted.getOrderNo(), delOutboundCompleted);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        // ????????????
                        this.delOutboundCompletedService.fail(delOutboundCompleted.getId(), e.getMessage());
                        // ????????????????????????????????????
                        if (e instanceof RejectedExecutionException) {
                            logger.error("=============================================");
                            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>???????????????????????????");
                            logger.error("=============================================");
                            break;
                        }
                    }
                }
            }
        }
    }

    private void handle(LambdaQueryWrapper<DelOutboundCompleted> queryWrapper, BiConsumer<String, Long> consumer, int limit, boolean needLock) {
        // ????????????200
        queryWrapper.last("limit " + limit);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        List<DelOutboundCompleted> delOutboundCompletedList = this.delOutboundCompletedService.list(queryWrapper);
        stopWatch.stop();
        logger.info(">>>>>[???????????????]??????????????????????????????"+stopWatch.getLastTaskTimeMillis());
        if (CollectionUtils.isNotEmpty(delOutboundCompletedList)) {
            for (DelOutboundCompleted delOutboundCompleted : delOutboundCompletedList) {
                if (needLock) {
                    // ????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    String key = applicationName + ":DelOutboundTimer:handle:" + delOutboundCompleted.getId();
                    RLock lock = redissonClient.getLock(key);
                    try {
                        if (lock.tryLock(0, TimeUnit.SECONDS)) {
                            consumer.accept(delOutboundCompleted.getOrderNo(), delOutboundCompleted.getId());
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        // ????????????
                        this.delOutboundCompletedService.fail(delOutboundCompleted.getId(), e.getMessage());

                        Integer handleSize = delOutboundCompleted.getHandleSize();

                        if(handleSize > 5){

                            LambdaUpdateWrapper<DelOutbound> update = Wrappers.lambdaUpdate();
                            update.set(DelOutbound::getDelFlag, "2").eq(DelOutbound::getOrderNo, delOutboundCompleted.getOrderNo());
                            delOutboundService.update(null,update);
                        }

                        // ????????????????????????????????????
                        if (e instanceof RejectedExecutionException) {
                            logger.error("=============================================");
                            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>???????????????????????????");
                            logger.error("=============================================");
                            break;
                        }
                    } finally {
                        if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                            lock.unlock();
                        }
                    }
                } else {
                    try {
                        consumer.accept(delOutboundCompleted.getOrderNo(), delOutboundCompleted.getId());
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                        // ????????????
                        this.delOutboundCompletedService.fail(delOutboundCompleted.getId(), e.getMessage());

                        Integer handleSize = delOutboundCompleted.getHandleSize();

                        if(handleSize > 5){
                            LambdaUpdateWrapper<DelOutbound> update = Wrappers.lambdaUpdate();
                            update.set(DelOutbound::getDelFlag, "2").eq(DelOutbound::getOrderNo, delOutboundCompleted.getOrderNo());
                            delOutboundService.update(null,update);
                        }

                        // ????????????????????????????????????
                        if (e instanceof RejectedExecutionException) {
                            logger.error("=============================================");
                            logger.error(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>???????????????????????????");
                            logger.error("=============================================");
                            break;
                        }
                    }
                }
            }
        }
    }
}
