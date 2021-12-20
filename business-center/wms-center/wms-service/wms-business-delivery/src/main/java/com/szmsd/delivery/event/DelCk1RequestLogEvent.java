package com.szmsd.delivery.event;

import org.springframework.context.ApplicationEvent;

public class DelCk1RequestLogEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DelCk1RequestLogEvent(Object source) {
        super(source);
    }
}
