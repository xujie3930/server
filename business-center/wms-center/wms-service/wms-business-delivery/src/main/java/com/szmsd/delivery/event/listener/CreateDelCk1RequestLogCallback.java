package com.szmsd.delivery.event.listener;

import com.szmsd.delivery.domain.DelCk1RequestLog;
import com.szmsd.delivery.enums.DelCk1RequestLogConstant;
import com.szmsd.delivery.event.DelCk1RequestLogEvent;
import com.szmsd.delivery.event.EventUtil;
import org.springframework.stereotype.Component;

@Component
public class CreateDelCk1RequestLogCallback implements DelCk1RequestLogCallback {

    @Override
    public void callback(DelCk1RequestLog ck1RequestLog, String responseBody) {
        // 获取状态
        DelCk1RequestLog delCk1RequestLog = new DelCk1RequestLog();
        delCk1RequestLog.setOrderNo(ck1RequestLog.getOrderNo());
        delCk1RequestLog.setType(DelCk1RequestLogConstant.Type.create_status.name());
        EventUtil.publishEvent(new DelCk1RequestLogEvent(delCk1RequestLog));
    }
}
