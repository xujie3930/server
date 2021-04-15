package com.szmsd.http.config.inner.url;

import com.szmsd.http.config.inner.api.ThirdPaymentApiConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:20
 */
public class ThirdPaymentUrlConfig extends UrlConfig implements UrlApiConfig {

    private ThirdPaymentApiConfig api;

    @Override
    public ThirdPaymentApiConfig getApi() {
        return api;
    }

    public void setApi(ThirdPaymentApiConfig api) {
        this.api = api;
    }
}
