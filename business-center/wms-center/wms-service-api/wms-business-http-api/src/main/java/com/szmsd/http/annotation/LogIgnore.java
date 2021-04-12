package com.szmsd.http.annotation;

import java.lang.annotation.*;

/**
 * @author zhangyuyuan
 * @date 2021-04-12 14:16
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface LogIgnore {

    /**
     * 需要过滤的字段
     *
     * @return 字段
     */
    String[] value() default {};
}
