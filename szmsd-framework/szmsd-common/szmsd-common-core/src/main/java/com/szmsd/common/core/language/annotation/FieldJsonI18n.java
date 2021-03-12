package com.szmsd.common.core.language.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.szmsd.common.core.language.componet.FieldLanguage;
import com.szmsd.common.core.language.enums.LanguageEnum;
import com.szmsd.common.core.language.enums.LocalLanguageTypeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = FieldLanguage.class)
public @interface FieldJsonI18n {

    /**
     * RedisLanguageTable
     * @return
     */
    String type() default "";

    /**
     * 默认值
     * @return
     */
    String value() default "";

    /**
     * 自定义获取语言 LanguageEnum.sysName 为当前系统语言
     * @return
     */
    LanguageEnum language() default LanguageEnum.sysName;

    /**
     * 本地语言类型 LocalLanguageTypeEnum --> LocalLanguageEnum
     * @return
     */
    LocalLanguageTypeEnum localLanguageType() default LocalLanguageTypeEnum.SYSTEM_LANGUAGE;

    /**
     * 格式 value&{0},{1}····
     * 是否是占位符，多个值用 “," 隔开
     * @return
     */
    boolean isPlaceholder() default false;

}
