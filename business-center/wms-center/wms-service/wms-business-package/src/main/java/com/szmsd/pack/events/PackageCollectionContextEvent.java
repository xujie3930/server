package com.szmsd.pack.events;

import org.springframework.context.ApplicationEvent;

public class PackageCollectionContextEvent extends ApplicationEvent {

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public PackageCollectionContextEvent(Object source) {
        super(source);
    }

    public static void publishEvent(Object source) {
        EventUtil.applicationContext.publishEvent(new PackageCollectionContextEvent(source));
    }
}
