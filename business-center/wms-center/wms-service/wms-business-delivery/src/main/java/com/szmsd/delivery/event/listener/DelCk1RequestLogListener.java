package com.szmsd.delivery.event.listener;

import com.szmsd.delivery.config.Ck1RequestConfig;
import com.szmsd.delivery.config.ThreadPoolExecutorConfiguration;
import com.szmsd.delivery.domain.DelCk1RequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.event.DelCk1RequestLogEvent;
import com.szmsd.delivery.service.IDelCk1RequestLogService;
import com.szmsd.http.enums.DomainEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Date;

@Component
public class DelCk1RequestLogListener {

    @Autowired
    private Ck1RequestConfig ck1RequestConfig;
    @Autowired
    private IDelCk1RequestLogService delCk1RequestLogService;

    @Async(value = ThreadPoolExecutorConfiguration.THREADPOOLEXECUTOR_CK1_SAVE)
    @EventListener
    public void onApplicationEvent(DelCk1RequestLogEvent event) {
        DelCk1RequestLog ck1RequestLog = (DelCk1RequestLog) event.getSource();
        ck1RequestLog.setState(DelCk1RequestLogConstant.State.WAIT.name());
        ck1RequestLog.setNextRetryTime(new Date());
        String type = ck1RequestLog.getType();
        String api = ck1RequestConfig.getApi(type);
        if (DelCk1RequestLogConstant.Type.cancel.name().equals(type)
                || DelCk1RequestLogConstant.Type.finished.name().equals(type)
                || DelCk1RequestLogConstant.Type.create_status.name().equals(type)) {
            api = MessageFormat.format(api, ck1RequestLog.getOrderNo());
        }
        String url = DomainEnum.Ck1OpenAPIDomain.wrapper(api);
        ck1RequestLog.setUrl(url);
        this.delCk1RequestLogService.save(ck1RequestLog);
    }
}
