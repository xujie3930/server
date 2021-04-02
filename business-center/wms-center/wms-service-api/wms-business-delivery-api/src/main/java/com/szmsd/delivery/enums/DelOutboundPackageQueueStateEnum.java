package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-04-02 13:44
 */
public enum DelOutboundPackageQueueStateEnum {

    INIT("INIT", "初始化"),
    SUCCESS("SUCCESS", "成功"),
    FAIL("FAIL", "失败"),
    ;

    private final String code;
    private final String name;

    DelOutboundPackageQueueStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundOrderTypeEnum get(String code) {
        for (DelOutboundOrderTypeEnum anEnum : DelOutboundOrderTypeEnum.values()) {
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
