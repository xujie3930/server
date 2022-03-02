package com.szmsd.http.config.inner.url;

import com.szmsd.http.config.inner.api.ApiConfig;
import com.szmsd.http.config.inner.api.SrmApiConfig;

public class SrmUrlConfig extends UrlConfig implements UrlApiConfig {

    private SrmApiConfig api;

    @Override
    public ApiConfig getApi() {
        return this.api;
    }

    public void setApi(SrmApiConfig api) {
        this.api = api;
    }
}
