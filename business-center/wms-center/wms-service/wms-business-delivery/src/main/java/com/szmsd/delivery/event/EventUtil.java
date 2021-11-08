package com.szmsd.delivery.event;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2020-12-23 023 10:50
 */
@Component
public final class EventUtil implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * @param event event
     */
    public static void publishEvent(ShipmentPackingEvent event) {
        EventUtil.applicationContext.publishEvent(event);
    }

    /**
     * @param event event
     */
    public static void publishEvent(DelOutboundOperationLogEvent event) {
        EventUtil.applicationContext.publishEvent(event);
    }

    /**
     * @param event event
     */
    public static void publishEvent(DelOutboundRetryLabelEvent event) {
        EventUtil.applicationContext.publishEvent(event);
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        EventUtil.applicationContext = applicationContext;
    }
}
