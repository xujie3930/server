package com.szmsd.putinstorage.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.szmsd.putinstorage.componet.FieldLanguage;
import com.szmsd.putinstorage.enums.LanguageEnum;
import com.szmsd.putinstorage.enums.LocalLanguageTypeEnum;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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

}
