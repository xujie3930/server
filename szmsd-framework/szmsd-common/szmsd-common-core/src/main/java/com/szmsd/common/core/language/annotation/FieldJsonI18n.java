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
     * 是否多个,隔开 value,value2,value3
     * @return
     */
    boolean isMultiple() default false;

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

}