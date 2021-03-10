package com.szmsd.http.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 9:45
 */
public class RequestLogEvent extends ApplicationEvent {

    public RequestLogEvent(Object source) {
        super(source);
    }
}
