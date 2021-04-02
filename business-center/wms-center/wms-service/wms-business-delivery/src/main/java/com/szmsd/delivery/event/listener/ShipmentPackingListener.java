package com.szmsd.delivery.event.listener;

import com.szmsd.delivery.event.ShipmentPackingEvent;
import com.szmsd.delivery.service.IDelOutboundPackageQueueService;
import com.szmsd.delivery.service.wrapper.IDelOutboundAsyncService;
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

    @Autowired
    private IDelOutboundAsyncService delOutboundAsyncService;
    @Autowired
    private IDelOutboundPackageQueueService delOutboundPackageQueueService;

    @Async
    @EventListener
    public void onApplicationEvent(ShipmentPackingEvent event) {
        if (null != event.getSource()) {
            this.delOutboundAsyncService.shipmentPacking((Long) event.getSource());
//            DelOutboundPackageQueue queue = new DelOutboundPackageQueue();
//            queue.setOrderId((Long) event.getSource());
//            queue.setNextHandleTime(new Date());
//            this.delOutboundPackageQueueService.insertDelOutboundPackageQueue(queue);
        }
    }
}
