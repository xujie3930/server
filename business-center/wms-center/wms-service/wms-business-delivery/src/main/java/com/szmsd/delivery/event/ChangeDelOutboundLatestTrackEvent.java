package com.szmsd.delivery.event;

import org.springframework.context.ApplicationEvent;

public class ChangeDelOutboundLatestTrackEvent extends ApplicationEvent {

    public ChangeDelOutboundLatestTrackEvent(Object source) {
        super(source);
    }
}
