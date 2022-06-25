package com.szmsd.http.config.inner.url;

import com.szmsd.http.config.inner.api.SaaSPricedApiConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:20
 */
public class SaaSPricedUrlConfig extends UrlConfig implements UrlApiConfig {

    private SaaSPricedApiConfig api;

    @Override
    public SaaSPricedApiConfig getApi() {
        return api;
    }

    public void setApi(SaaSPricedApiConfig api) {
        this.api = api;
    }
}
