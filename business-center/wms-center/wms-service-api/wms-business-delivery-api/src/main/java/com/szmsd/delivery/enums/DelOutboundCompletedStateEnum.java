package com.szmsd.delivery.enums;

/**
 * @author zhangyuyuan
 * @date 2021-04-06 15:24
 */
public enum DelOutboundCompletedStateEnum {

    INIT("INIT", "初始化"),
    SUCCESS("SUCCESS", "成功"),
    FAIL("FAIL", "失败"),
    ;

    private final String code;
    private final String name;

    DelOutboundCompletedStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
