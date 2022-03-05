package com.szmsd.delivery.event;

import org.springframework.context.ApplicationEvent;

public class DelSrmCostLogEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DelSrmCostLogEvent(Object source) {
        super(source);

    }
}
