package com.szmsd.delivery.enums;

public enum DelQueryServiceFeedbackEnum {


    CREATE("SUBMITTED", "创建"),
    FEEDBACK("FEEDBACK", "反馈"),;

    private final String code;
    private final String name;

    DelQueryServiceFeedbackEnum(String code, String name) {
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
