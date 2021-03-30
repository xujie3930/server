package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 16:48
 */
public enum DelOutboundExceptionStateEnum {

    NORMAL("NORMAL", "正常"),
    ABNORMAL("ABNORMAL", "异常"),

    ;

    private final String code;
    private final String name;

    DelOutboundExceptionStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundExceptionStateEnum get(String code) {
        for (DelOutboundExceptionStateEnum anEnum : DelOutboundExceptionStateEnum.values()) {
            if (anEnum.getCode().equals(code)) {
                return anEnum;
            }
        }
        return null;
    }

    public static boolean has(String code) {
        return Objects.nonNull(get(code));
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
