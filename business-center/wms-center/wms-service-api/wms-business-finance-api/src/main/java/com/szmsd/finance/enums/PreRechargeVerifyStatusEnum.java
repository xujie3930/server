package com.szmsd.finance.enums;

public enum PreRechargeVerifyStatusEnum {

    REVIEWED("已审核", 1),
    UNAPPROVED("未审核", 0),
    FAILED("审核未通过",2),
    REJECT("驳回",3);

    private String desc;
    private Integer value;

    PreRechargeVerifyStatusEnum(String s,Integer i){
        this.desc = s;
        this.value = i;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
