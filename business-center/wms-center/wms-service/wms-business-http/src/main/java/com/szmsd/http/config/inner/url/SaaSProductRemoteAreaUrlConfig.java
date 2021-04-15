package com.szmsd.http.config.inner.url;

import com.szmsd.http.config.inner.api.SaaSProductRemoteAreaApiConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:20
 */
public class SaaSProductRemoteAreaUrlConfig extends UrlConfig implements UrlApiConfig {

    private SaaSProductRemoteAreaApiConfig api;

    @Override
    public SaaSProductRemoteAreaApiConfig getApi() {
        return api;
    }

    public void setApi(SaaSProductRemoteAreaApiConfig api) {
        this.api = api;
    }
}
