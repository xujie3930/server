package com.szmsd.http.servlet.header;

/**
 * @author zhangyuyuan
 * @date 2021-04-30 13:43
 */
public interface ContentType {

    /**
     * 验证contentType
     *
     * @param contentType contentType
     * @return boolean
     */
    boolean has(String contentType);
}
