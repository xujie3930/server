package com.szmsd.http.service.http;

import com.szmsd.http.config.HttpConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 16:11
 */
public class WmsRequest extends AbstractRequest {

    public WmsRequest(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    HttpUrlType getHttpUrlType() {
        return HttpUrlType.WMS;
    }

}
