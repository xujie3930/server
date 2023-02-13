package com.szmsd.track.event;

import org.springframework.context.ApplicationEvent;

public class TrackTyRequestLogEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public TrackTyRequestLogEvent(Object source) {
        super(source);
    }
}
