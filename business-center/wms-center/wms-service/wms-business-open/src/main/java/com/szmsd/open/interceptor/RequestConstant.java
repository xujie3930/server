package com.szmsd.open.interceptor;

import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 15:16
 */
public final class RequestConstant {

    public static final String APP_ID = "appId";
    public static final String SIGN = "sign";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String ENCODING = StandardCharsets.UTF_8.name();
    public static final MediaType MEDIA_TYPE = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
}
