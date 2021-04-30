package com.szmsd.http.servlet.header;

import javax.servlet.http.HttpServletRequest;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 13:43
 */
public class RequestContentType implements ContentType {

    private final HttpServletRequest request;

    public RequestContentType(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public boolean has(String contentType) {
        return false;
    }
}
