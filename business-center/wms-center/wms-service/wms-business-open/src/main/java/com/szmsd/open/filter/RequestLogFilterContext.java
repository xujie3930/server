package com.szmsd.open.filter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 17:12
 */
public class RequestLogFilterContext {

    private static final ThreadLocal<RequestLogFilterContext> contextHolder = new ThreadLocal<>();
    private String requestId;
    private String transactionId;
    private String appId;
    private String requestUri;
    private Map<String, Object> parameterMap = new HashMap<>();

    public RequestLogFilterContext() {
    }

    public static synchronized RequestLogFilterContext getCurrentContext() {
        RequestLogFilterContext context = contextHolder.get();
        if (context == null) {
            contextHolder.set(new RequestLogFilterContext());
            context = contextHolder.get();
        }
        return context;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, Object> parameterMap) {
        this.parameterMap = parameterMap;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
