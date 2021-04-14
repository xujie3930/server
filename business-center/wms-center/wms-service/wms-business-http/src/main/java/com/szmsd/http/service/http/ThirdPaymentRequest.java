package com.szmsd.http.service.http;

import com.szmsd.http.config.HttpConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 16:58
 */
public class ThirdPaymentRequest extends AbstractRequest {

    public ThirdPaymentRequest(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    HttpUrlType getHttpUrlType() {
        return HttpUrlType.THIRD_PAYMENT;
    }
}
