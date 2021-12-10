package com.szmsd.http.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class HttpResponseVO implements Serializable {

    /**
     * 响应状态码
     */
    private int status;

    /**
     * 响应头
     */
    private Map<String, String> headers;

    /**
     * 响应内容（二进制根据需求自己转成对象）
     */
    private byte[] body;
}
