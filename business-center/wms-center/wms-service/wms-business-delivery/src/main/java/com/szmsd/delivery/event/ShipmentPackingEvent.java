package com.szmsd.delivery.event;

import org.springframework.context.ApplicationEvent;

/**
 * #D2 接收出库包裹信息
 *
 * @author zhangyuyuan
 * @date 2021-03-08 14:51
 */
public class ShipmentPackingEvent extends ApplicationEvent {

    public ShipmentPackingEvent(Object source) {
        super(source);
    }
}
