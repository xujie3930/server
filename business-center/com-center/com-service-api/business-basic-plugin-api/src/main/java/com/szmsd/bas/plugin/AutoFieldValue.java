package com.szmsd.bas.plugin;

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
     * 主类别编码
     *
     * @return String
     */
    String code();

    /**
     * 从数据字典上去取值的字段，有 subCode，subValue 两个
     * 默认 subCode
     *
     * @return String
     */
    String valueField() default "";

    /**
     * name字段名称，默认在当前字段后面增加Name
     *
     * @return String
     */
    String nameField() default "";
}
