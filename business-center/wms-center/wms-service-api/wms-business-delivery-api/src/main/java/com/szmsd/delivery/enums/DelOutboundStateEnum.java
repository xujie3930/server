package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 16:48
 */
public enum DelOutboundStateEnum {

    REVIEWED("REVIEWED", "待提审"),
    UNDER_REVIEW("UNDER_REVIEW", "提审中"),
    DELIVERED("DELIVERED", "待发货"),
    PROCESSING("PROCESSING", "仓库处理中"),
    AUDIT_FAILED("AUDIT_FAILED", "审核失败"),
    COMPLETED("COMPLETED", "已完成"),
    CANCELLED("CANCELLED", "已取消"),

    ;

    private final String code;
    private final String name;

    DelOutboundStateEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundStateEnum get(String code) {
        for (DelOutboundStateEnum anEnum : DelOutboundStateEnum.values()) {
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
