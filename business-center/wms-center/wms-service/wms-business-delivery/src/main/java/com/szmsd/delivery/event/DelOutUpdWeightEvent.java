package com.szmsd.delivery.event;

import org.springframework.context.ApplicationEvent;

public class DelOutUpdWeightEvent extends ApplicationEvent {

    public DelOutUpdWeightEvent(Object source) {
        super(source);
    }
}
