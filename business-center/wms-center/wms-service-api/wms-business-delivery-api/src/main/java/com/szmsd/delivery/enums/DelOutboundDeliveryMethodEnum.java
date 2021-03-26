package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 16:48
 */
public enum DelOutboundDeliveryMethodEnum {

    ESD("ESD", "快递自提"),
    VPU("VPU", "车辆自提"),

    ;

    private final String code;
    private final String name;

    DelOutboundDeliveryMethodEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundDeliveryMethodEnum get(String code) {
        for (DelOutboundDeliveryMethodEnum anEnum : DelOutboundDeliveryMethodEnum.values()) {
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
