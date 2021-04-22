package com.szmsd.open.interceptor;

/**
 * @author zhangyuyuan
 * @date 2021-04-22 16:01
 */
public interface AuthHandler {

    /**
     * 认证
     *
     * @param appId appId
     * @param sign  sign
     * @throws AuthHandlerException AuthHandlerException
     */
    void authentication(String appId, String sign) throws AuthHandlerException;
}
