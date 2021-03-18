package com.szmsd.http.service.impl;

import com.alibaba.fastjson.JSON;
import com.szmsd.common.core.utils.HttpClientHelper;
import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.domain.HtpRequestLog;
import com.szmsd.http.event.EventUtil;
import com.szmsd.http.event.RequestLogEvent;
import org.slf4j.MDC;

import java.util.Date;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 14:00
 */
public abstract class AbstractHttpRequest {

    protected HttpConfig httpConfig;

    public AbstractHttpRequest(HttpConfig httpConfig) {
        this.httpConfig = httpConfig;
    }

    abstract String getUrl();

    abstract Map<String, String> getHeaderMap();

    String httpPost(String api, Object object) {
        String url = getUrl() + api;
        String requestBody = JSON.toJSONString(object);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String responseBody = HttpClientHelper.httpPost(url, requestBody, headerMap);
        addLog(url, "POST", headerMap, requestBody, requestTime, responseBody);
        return responseBody;
    }

    String httpPut(String api, Object object) {
        String url = getUrl() + api;
        String requestBody = JSON.toJSONString(object);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String responseBody = HttpClientHelper.httpPut(url, requestBody, headerMap);
        addLog(url, "PUT", headerMap, requestBody, requestTime, responseBody);
        return responseBody;
    }

    String httpDelete(String api, Object object) {
        String url = getUrl() + api;
        String requestBody = JSON.toJSONString(object);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String responseBody = HttpClientHelper.httpDelete(url, requestBody, headerMap);
        addLog(url, "DELETE", headerMap, requestBody, requestTime, responseBody);
        return responseBody;
    }

    String httpGet(String api, Object object) {
        String url = getUrl() + api;
        String requestBody = JSON.toJSONString(object);
        Map<String, String> headerMap = getHeaderMap();
        Date requestTime = new Date();
        String responseBody = HttpClientHelper.httpGet(url, requestBody, headerMap);
        addLog(url, "GET", headerMap, requestBody, requestTime, responseBody);
        return responseBody;
    }

    void addLog(String url, String method, Map<String, String> headerMap, String requestBody, Date requestTime, String responseBody) {
        Date responseTime = new Date();
        HtpRequestLog log = new HtpRequestLog();
        log.setTraceId(MDC.get("TID"));
        log.setRequestUri(url);
        log.setRequestMethod(method);
        log.setRequestHeader(JSON.toJSONString(headerMap));
        log.setRequestBody(requestBody);
        log.setRequestTime(requestTime);
        log.setResponseBody(responseBody);
        log.setResponseTime(responseTime);
        EventUtil.publishEvent(new RequestLogEvent(log));
    }
}
