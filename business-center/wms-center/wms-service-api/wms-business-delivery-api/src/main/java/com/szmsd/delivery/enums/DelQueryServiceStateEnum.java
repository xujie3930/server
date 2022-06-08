package com.szmsd.delivery.enums;

import java.util.Objects;

public enum DelQueryServiceStateEnum {

    SUBMITTED("Submitted", "已提交"),
    UNDER_EXAMINATION("UNDER_EXAMINATION", "查件中"),
    COMPLETED("COMPLETED", "已完成");

    private final String code;
    private final String name;

    DelQueryServiceStateEnum(String code, String name) {
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
