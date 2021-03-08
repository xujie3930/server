package com.szmsd.open.event.listener;

import com.szmsd.open.domain.OpnRequestLog;
import com.szmsd.open.event.RequestLogEvent;
import com.szmsd.open.service.IOpnRequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 9:46
 */
@Component
public class RequestLogListener {

    @Autowired
    private IOpnRequestLogService opnRequestLogService;

    @Async
    @EventListener
    public void onApplicationEvent(RequestLogEvent event) {
        if (null != event.getSource()) {
            this.opnRequestLogService.add((OpnRequestLog) event.getSource());
        }
    }
}
