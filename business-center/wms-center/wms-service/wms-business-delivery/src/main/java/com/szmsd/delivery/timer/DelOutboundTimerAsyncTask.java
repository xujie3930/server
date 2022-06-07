package com.szmsd.delivery.timer;

import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.service.IDelOutboundBringVerifyAsyncService;
import com.szmsd.delivery.service.IDelOutboundCompletedService;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class DelOutboundTimerAsyncTask {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IDelOutboundAsyncService delOutboundAsyncService;
    @Autowired
    private IDelOutboundCompletedService delOutboundCompletedService;
    @Autowired
    private IDelOutboundBringVerifyAsyncService delOutboundBringVerifyAsyncService;

    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_DELOUTBOUND_PROCESSING)
    public void asyncHandleProcessing(String orderNo, Long id) {
        this.handle(s -> delOutboundAsyncService.processing(orderNo), id);
    }

    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_DELOUTBOUND_SHIPPED)
    public void asyncHandleCompleted(String orderNo, Long id) {
        this.handle(s -> this.delOutboundAsyncService.completed(orderNo), id);
    }

    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_DELOUTBOUND_CANCELED)
    public void asyncHandleCancelled(String orderNo, Long id) {
        this.handle(s -> delOutboundAsyncService.cancelled(orderNo), id);
    }

    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_DELOUTBOUND_REVIEWED)
    public void asyncBringVerify(String orderNo, Long id) {
        this.handle(s -> delOutboundBringVerifyAsyncService.bringVerifyAsync(orderNo), id);
    }

    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_DELOUTBOUND_REVIEWED2)
    public void asyncBringVerify2(String orderNo, Long id) {
        this.handle(s -> delOutboundBringVerifyAsyncService.bringVerifyAsync(orderNo), id);
    }

    @Async(ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_SHIPMENTPACKINGEVENT)
    public void asyncShipmentPacking(String orderNo, Long id) {
        this.handle(s -> delOutboundAsyncService.shipmentPacking(orderNo), id);
    }

    private void handle(Consumer<String> consumer, Long id) {
        try {
            consumer.accept(null);
            // 处理成功
            this.delOutboundCompletedService.success(id);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            // 处理失败
            this.delOutboundCompletedService.fail(id, e.getMessage());
        }
    }
}
