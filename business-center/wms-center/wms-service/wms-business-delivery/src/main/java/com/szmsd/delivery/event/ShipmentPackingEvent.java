package com.szmsd.delivery.event;

import com.szmsd.delivery.config.AsyncThreadObject;
import org.slf4j.MDC;
import org.springframework.context.ApplicationEvent;

/**
 * #D2 接收出库包裹信息
 *
 * @author zhangyuyuan
 * @date 2021-03-08 14:51
 */
public class ShipmentPackingEvent extends ApplicationEvent {

    private final AsyncThreadObject asyncThreadObject;

    public ShipmentPackingEvent(Object source) {
        super(source);
        this.asyncThreadObject = new AsyncThreadObject();
    }

    public AsyncThreadObject getAsyncThreadObject() {
        return asyncThreadObject;
    }
}
