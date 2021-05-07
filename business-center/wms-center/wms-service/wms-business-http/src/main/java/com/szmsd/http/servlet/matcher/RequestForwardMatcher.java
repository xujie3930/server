package com.szmsd.http.servlet.matcher;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 9:19
 */
public interface RequestForwardMatcher {

    /**
     * 匹配路径是否做处理
     *
     * @param path 路径
     * @return boolean
     */
    boolean match(String path);
}
