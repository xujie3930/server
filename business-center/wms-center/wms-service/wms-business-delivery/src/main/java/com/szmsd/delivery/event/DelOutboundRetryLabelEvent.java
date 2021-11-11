package com.szmsd.delivery.event;

import org.springframework.context.ApplicationEvent;

public class DelOutboundRetryLabelEvent extends ApplicationEvent {

    private final boolean force;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public DelOutboundRetryLabelEvent(Object source) {
        super(source);
        this.force = false;
    }

    public DelOutboundRetryLabelEvent(Object source, boolean force) {
        super(source);
        this.force = force;
    }

    public boolean isForce() {
        return force;
    }
}
