package com.szmsd.open.interceptor;

/**
 * @author zhangyuyuan
 * @date 2021-04-22 16:06
 */
public class AuthHandlerException extends Exception {

    public AuthHandlerException() {
    }

    public AuthHandlerException(String message) {
        super(message);
    }

    public AuthHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthHandlerException(Throwable cause) {
        super(cause);
    }

    public AuthHandlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
