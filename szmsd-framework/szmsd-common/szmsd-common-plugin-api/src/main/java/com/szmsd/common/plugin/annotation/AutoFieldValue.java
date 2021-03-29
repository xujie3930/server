package com.szmsd.common.plugin.annotation;

import com.szmsd.common.plugin.interfaces.AbstractCommonParameter;

import java.lang.annotation.*;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 10:25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface AutoFieldValue {

    /**
     * 支持的类型
     *
     * @return String
     */
    String supports();

    /**
     * 主类别编码
     *
     * @return String
     */
    String code() default "";

    /**
     * name字段名称，默认在当前字段后面增加Name
     *
     * @return String
     */
    String nameField() default "";

    /**
     * CommonParameter
     *
     * @return CommonParameter
     */
    Class<? extends AbstractCommonParameter> cp();
}
