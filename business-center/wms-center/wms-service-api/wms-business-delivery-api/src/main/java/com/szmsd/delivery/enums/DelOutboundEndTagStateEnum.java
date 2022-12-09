package com.szmsd.delivery.enums;

public enum DelOutboundEndTagStateEnum {

    REVIEWED(1, "是"),
    ;

    private final Integer code;
    private final String name;

    DelOutboundEndTagStateEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
