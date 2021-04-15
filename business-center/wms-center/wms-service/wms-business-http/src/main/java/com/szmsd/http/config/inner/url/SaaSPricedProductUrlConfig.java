package com.szmsd.http.config.inner.url;

import com.szmsd.http.config.inner.api.SaaSPricedProductApiConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:20
 */
public class SaaSPricedProductUrlConfig extends UrlConfig implements UrlApiConfig {

    private SaaSPricedProductApiConfig api;

    @Override
    public SaaSPricedProductApiConfig getApi() {
        return api;
    }

    public void setApi(SaaSPricedProductApiConfig api) {
        this.api = api;
    }
}
