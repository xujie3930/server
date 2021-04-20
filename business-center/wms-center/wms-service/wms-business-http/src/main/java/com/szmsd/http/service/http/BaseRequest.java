package com.szmsd.http.service.http;

import com.alibaba.fastjson.JSON;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.event.EventUtil;
import com.szmsd.http.event.RequestLogEvent;
import org.slf4j.MDC;

import java.util.Date;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-20 11:10
 */
public class BaseRequest {

    void addLog(String warehouseCode, String urlGroup, String url, String method, Map<String, String> headerMap, String requestBody, Date requestTime, String responseBody) {
        Date responseTime = new Date();
        HtpRequestLog log = new HtpRequestLog();
        log.setTraceId(MDC.get("TID"));
        log.setWarehouseCode(warehouseCode);
        log.setRequestUrlGroup(urlGroup);
        log.setRequestUri(url);
        log.setRequestMethod(method);
        log.setRequestHeader(JSON.toJSONString(headerMap));
        log.setRequestBody(requestBody);
        log.setRequestTime(requestTime);
        log.setResponseBody(responseBody);
        log.setResponseTime(responseTime);
        EventUtil.publishEvent(new RequestLogEvent(log));
    }

    boolean isNotEmpty(String str) {
        return !this.isEmpty(str);
    }

    boolean isEmpty(String str) {
        return null == str || "".equals(str.trim());
    }
}
