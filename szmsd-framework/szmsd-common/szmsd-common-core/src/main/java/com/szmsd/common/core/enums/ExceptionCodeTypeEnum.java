package com.szmsd.common.core.enums;


import lombok.Getter;

/**
 * @FileName ExceptionCodeTypeEnum.java
 * @Description 模块异常编码规则枚举
 * @Date 2020-06-15 14:06
 * @Author Yan Hua
 * @Version 1.0
 */
@Getter
public enum ExceptionCodeTypeEnum {

    /**
     * 普通提示类型
     */
    MESSAGE("M"),
    /**
     * 错误类型
     */
    ERROR("E");

    private String prefix;

    ExceptionCodeTypeEnum(String prefix) {
        this.prefix = prefix;
    }

}