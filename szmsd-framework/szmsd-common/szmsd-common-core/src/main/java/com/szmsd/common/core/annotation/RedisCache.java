package com.szmsd.common.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RedisCache {

    /**
     * 字段类型
     *
     * @return
     */
    RedisLanguageFieldEnum redisLanguageFieldEnum();

    int paramIndex() default 0;

}
