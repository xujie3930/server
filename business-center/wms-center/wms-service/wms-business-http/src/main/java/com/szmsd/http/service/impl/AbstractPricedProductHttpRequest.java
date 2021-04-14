package com.szmsd.http.service.impl;

import com.szmsd.http.config.HttpConfig;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 16:20
 */
@Deprecated
public abstract class AbstractPricedProductHttpRequest extends AbstractHttpRequest {

    public AbstractPricedProductHttpRequest(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    String getUrl() {
        return this.httpConfig.getPricedProductUrl();
    }

    @Override
    Map<String, String> getHeaderMap() {
        return this.httpConfig.getPricedProductHeaderMap();
    }
}
