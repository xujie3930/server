package com.szmsd.http.service.impl;

import com.szmsd.http.config.HttpConfig;

import java.util.Map;

/**
 * @ClassName: AbstractReturnExpressReq
 * @Description:
 * @Author: 11
 * @Date: 2021/3/27 13:34
 */
@Deprecated
public abstract class AbstractReturnExpressReq extends AbstractHttpRequest {

    public AbstractReturnExpressReq(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    String getUrl() {
        return this.httpConfig.getBaseUrl();
    }

    @Override
    Map<String, String> getHeaderMap() {
        return httpConfig.getBaseHeaderMap();
    }
}
