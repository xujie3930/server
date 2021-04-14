package com.szmsd.http.config.inner.url;

import com.szmsd.http.config.inner.api.SaaSCarrierServiceAdminApiConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:21
 */
public class SaaSCarrierServiceAdminUrlConfig extends UrlConfig implements UrlApiConfig {

    private SaaSCarrierServiceAdminApiConfig api;

    @Override
    public SaaSCarrierServiceAdminApiConfig getApi() {
        return api;
    }

    public void setApi(SaaSCarrierServiceAdminApiConfig api) {
        this.api = api;
    }
}
