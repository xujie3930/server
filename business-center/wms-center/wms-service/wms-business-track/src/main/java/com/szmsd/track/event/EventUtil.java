package com.szmsd.track.event;

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

    public static void publishEvent(TrackTyRequestLogEvent event) {
        EventUtil.applicationContext.publishEvent(event);
    }

    @Override
    public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
        EventUtil.applicationContext = applicationContext;
    }
}
