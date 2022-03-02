package com.szmsd.http.service.http;

import com.szmsd.http.config.HttpConfig;
import com.szmsd.http.enums.HttpUrlType;

public class SrmRequest extends AbstractRequest {

    protected SrmRequest(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    HttpUrlType getHttpUrlType() {
        return HttpUrlType.SRM;
    }
}
