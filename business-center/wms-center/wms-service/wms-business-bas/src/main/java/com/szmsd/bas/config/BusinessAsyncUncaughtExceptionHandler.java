package com.szmsd.bas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * @author zhangyuyuan
 * @date 2020-12-23 023 10:47
 */
public class BusinessAsyncUncaughtExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(BusinessAsyncUncaughtExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
        throwable.printStackTrace();
        logger.error("TASK Exception message - " + throwable.getMessage());
        logger.error("Method name - " + method.getName());
        for (Object param : params) {
            logger.error("Parameter value - " + param);
        }
    }
}
