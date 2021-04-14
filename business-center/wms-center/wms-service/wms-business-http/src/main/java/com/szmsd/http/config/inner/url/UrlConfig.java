package com.szmsd.http.config.inner.url;

import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-04-13 14:19
 */
public class UrlConfig {

    /**
     * header请求参数配置
     */
    private Map<String, String> headers;

    /**
     * 请求路径
     */
    private String url;

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
