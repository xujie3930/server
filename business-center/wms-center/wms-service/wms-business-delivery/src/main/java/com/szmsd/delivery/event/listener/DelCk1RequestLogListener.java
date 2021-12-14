package com.szmsd.delivery.event.listener;

import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelCk1RequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.event.DelCk1RequestLogEvent;
import com.szmsd.delivery.service.IDelCk1RequestLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class DelCk1RequestLogListener {

    @Autowired
    private IDelCk1RequestLogService delCk1RequestLogService;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_CK1_SAVE)
    @EventListener
    public void onApplicationEvent(DelCk1RequestLogEvent event) {
        DelCk1RequestLog ck1RequestLog = (DelCk1RequestLog) event.getSource();
        ck1RequestLog.setState(DelCk1RequestLogConstant.State.WAIT.name());
        ck1RequestLog.setNextRetryTime(new Date());
        this.delCk1RequestLogService.save(ck1RequestLog);
    }
}
