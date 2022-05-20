package com.szmsd.ec.common.event;

import org.springframework.context.ApplicationEvent;

public class ShopifyFulfillmentEvent extends ApplicationEvent {

    public ShopifyFulfillmentEvent(Object source) {
        super(source);
    }
}
