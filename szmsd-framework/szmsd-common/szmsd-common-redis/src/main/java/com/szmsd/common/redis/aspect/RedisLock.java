package com.szmsd.common.redis.aspect;

import java.lang.annotation.*;

/**
 * 使用示例
 * ### @RedisLock(value = "id", serviceName = SERVICE_NAME)
 * ### @RedisLock(value = "dto.id", serviceName = SERVICE_NAME)
 * ### @RedisLock(value = "dto.id", serviceName = SERVICE_NAME, IgnoreNull = true)
 * ### @RedisLock(value = "ids", serviceName = SERVICE_NAME)
 * ### @RedisLock(value = "ids", serviceName = SERVICE_NAME, contextId = CONTEXT_ID)
 *
 * @author zhangyuyuan
 * @date 2020-11-26 026 17:09
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {

    /**
     * 单据号，用于redis的key
     *
     * @return String
     */
    String value();

    /**
     * 业务模块
     *
     * @return String
     */
    String serviceName();

    /**
     * 业务统一处理ID
     *
     * @return String
     */
    String contextId() default "";

    /**
     * 是否忽略空值
     *
     * @return boolean
     */
    boolean IgnoreNull() default false;
}
