package com.szmsd.http.servlet.header;

import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 13:43
 */
public class ResponseContentType implements ContentType {

    private final HttpServletResponse response;

    public ResponseContentType(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public boolean has(String contentType) {
        return false;
    }
}
