package com.szmsd.common.datascope.annotation;

import java.lang.annotation.*;

/**
 * 数据权限过滤注解
 *
 * @author szmsd
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 客户代码字段
     *
     * @return String
     */
    String value();

}
