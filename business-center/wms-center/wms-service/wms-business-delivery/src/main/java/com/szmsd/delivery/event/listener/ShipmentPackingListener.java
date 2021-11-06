package com.szmsd.delivery.event.listener;

import com.alibaba.fastjson.JSON;
import com.szmsd.delivery.config.AsyncThreadObject;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.event.ShipmentPackingEvent;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 14:51
 */
@Component
public class ShipmentPackingListener {
    private final Logger logger = LoggerFactory.getLogger(ShipmentPackingListener.class);

    @Autowired
    private IDelOutboundAsyncService delOutboundAsyncService;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_SHIPMENTPACKINGEVENT)
    @EventListener
    public void onApplicationEvent(ShipmentPackingEvent event) {
        Thread thread = Thread.currentThread();
        AsyncThreadObject asyncThreadObject = event.getAsyncThreadObject();
        boolean isAsyncThread = !asyncThreadObject.isAsyncThread();
        logger.info("(1)任务开始执行，当前任务名称：{}，当前任务ID：{}，是否为异步任务：{}，任务相关参数：{}", thread.getName(), thread.getId(), isAsyncThread, JSON.toJSONString(event));
        if (isAsyncThread) {
            // 是异步的线程，将主线程的tid赋值到当前线程的TID上
            asyncThreadObject.loadTid();
        }
        try {
            if (null != event.getSource()) {
                this.delOutboundAsyncService.shipmentPacking((Long) event.getSource());
            }
        } finally {
            if (isAsyncThread) {
                // 异步线程执行完成之后将值清空
                asyncThreadObject.unloadTid();
            }
        }
    }
}
