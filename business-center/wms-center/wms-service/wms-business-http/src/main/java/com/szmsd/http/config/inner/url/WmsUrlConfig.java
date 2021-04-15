package com.szmsd.http.config.inner.url;

import com.szmsd.http.config.inner.api.WmsApiConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:18
 */
public class WmsUrlConfig extends UrlConfig implements UrlApiConfig {

    private WmsApiConfig api;

    @Override
    public WmsApiConfig getApi() {
        return api;
    }

    public void setApi(WmsApiConfig api) {
        this.api = api;
    }
}
