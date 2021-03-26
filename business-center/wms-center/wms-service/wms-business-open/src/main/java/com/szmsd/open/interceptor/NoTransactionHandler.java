package com.szmsd.open.interceptor;

import java.lang.annotation.*;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 19:04
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface NoTransactionHandler {
}
