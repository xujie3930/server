package com.szmsd.bas.driver;

import com.szmsd.common.core.enums.CodeToNameEnum;

import java.lang.annotation.*;

/**
 * @author liyingfeng
 * @date 2020/11/20 10:49
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface UpdateRedis {

    /**
     * 字段类型
     * @return
     */
    CodeToNameEnum type() ;

}
