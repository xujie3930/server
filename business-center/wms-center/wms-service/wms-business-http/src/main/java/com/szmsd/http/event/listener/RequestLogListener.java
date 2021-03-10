package com.szmsd.http.event.listener;

import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.event.RequestLogEvent;
import com.szmsd.http.service.IHtpRequestLogService;
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
    private IHtpRequestLogService htpRequestLogService;

    @Async
    @EventListener
    public void onApplicationEvent(RequestLogEvent event) {
        if (null != event.getSource()) {
            this.htpRequestLogService.add((HtpRequestLog) event.getSource());
        }
    }
}
