package com.szmsd.http.service.http.resolver;

import com.szmsd.common.core.utils.HttpResponseBody;
import com.szmsd.http.enums.HttpUrlType;

/**
 * @author zhangyuyuan
 * @date 2021-04-27 16:11
 */
public class ResponseResolverActuatorParameter implements ActuatorParameter {

    private HttpUrlType httpUrlType;
    private HttpResponseBody httpResponseBody;

    public ResponseResolverActuatorParameter(HttpUrlType httpUrlType, HttpResponseBody httpResponseBody) {
        this.httpUrlType = httpUrlType;
        this.httpResponseBody = httpResponseBody;
    }

    public HttpUrlType getHttpUrlType() {
        return httpUrlType;
    }

    public void setHttpUrlType(HttpUrlType httpUrlType) {
        this.httpUrlType = httpUrlType;
    }

    public HttpResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }

    public void setHttpResponseBody(HttpResponseBody httpResponseBody) {
        this.httpResponseBody = httpResponseBody;
    }
}
