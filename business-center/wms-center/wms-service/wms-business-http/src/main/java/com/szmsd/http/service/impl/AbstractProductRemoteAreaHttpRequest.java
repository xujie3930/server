package com.szmsd.http.service.impl;

import com.szmsd.http.config.HttpConfig;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 16:20
 */
public abstract class AbstractProductRemoteAreaHttpRequest extends AbstractHttpRequest {

    public AbstractProductRemoteAreaHttpRequest(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    String getUrl() {
        return this.httpConfig.getProductRemoteAreaUrl();
    }

    @Override
    Map<String, String> getHeaderMap() {
        return this.httpConfig.getProductRemoteAreaHeaderMap();
    }
}
