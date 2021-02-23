package com.szmsd.common.core.annotation;

import com.szmsd.common.core.enums.CodeToNameEnum;

import java.lang.annotation.*;

/**
 * @author liyingfeng
 * @date 2020/11/19 14:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface CodeToNameElement {

    /**
     * 字段类型
     *
     * @return
     */
    CodeToNameEnum type();

    /**
     * 对应的编码
     *
     * @return
     */
    String keyCode();

    /**
     * 默认值
     *
     * @return
     */
    String name() default "";

}
