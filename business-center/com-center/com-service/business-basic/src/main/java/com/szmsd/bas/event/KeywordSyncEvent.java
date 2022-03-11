package com.szmsd.bas.event;

import org.springframework.context.ApplicationEvent;

public class KeywordSyncEvent extends ApplicationEvent {

    public KeywordSyncEvent(String carrierCode) {
        super(carrierCode);
    }
}
