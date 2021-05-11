package com.szmsd.common.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 11:23
 */
public interface TokenVerification {

    /**
     * 处理请求
     *
     * @param request  request
     * @param response response
     * @return boolean
     * @throws TokenException TokenException
     */
    boolean handler(HttpServletRequest request, HttpServletResponse response) throws TokenException;
}
