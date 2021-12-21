package com.szmsd.delivery.event.listener;

import com.szmsd.delivery.domain.DelCk1RequestLog;

public interface DelCk1RequestLogCallback {

    void callback(DelCk1RequestLog requestLog, String responseBody);
}
