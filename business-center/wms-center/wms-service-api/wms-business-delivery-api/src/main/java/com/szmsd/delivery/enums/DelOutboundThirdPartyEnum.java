package com.szmsd.delivery.enums;

import java.util.Objects;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 16:22
 */
public enum DelOutboundThirdPartyEnum {

    THIRD_PARTY("ThirdParty", "承运商"),
    WNS("WMS", "WMS"),

    ;

    private final String code;
    private final String name;

    DelOutboundThirdPartyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static DelOutboundThirdPartyEnum get(String code) {
        for (DelOutboundThirdPartyEnum anEnum : DelOutboundThirdPartyEnum.values()) {
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
