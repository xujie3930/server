package com.szmsd.open.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 14:51
 */
public class TransactionEvent extends ApplicationEvent {

    public TransactionEvent(Object source) {
        super(source);
    }
}
