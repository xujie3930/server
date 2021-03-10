package com.szmsd.http.service.impl;

import com.szmsd.http.config.HttpConfig;

/**
 * @author zhangyuyuan
 * @date 2021-03-10 16:20
 */
public abstract class AbstractBaseHttpRequest extends AbstractHttpRequest {

    public AbstractBaseHttpRequest(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    String getUrl() {
        return this.httpConfig.getBaseUrl();
    }
}
