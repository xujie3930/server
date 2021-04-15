package com.szmsd.http.service.http;

import com.szmsd.http.config.HttpConfig;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 16:58
 */
public class SaaSCarrierServiceAdminRequest extends AbstractRequest {

    public SaaSCarrierServiceAdminRequest(HttpConfig httpConfig) {
        super(httpConfig);
    }

    @Override
    HttpUrlType getHttpUrlType() {
        return HttpUrlType.CARRIER_SERVICE;
    }
}
