package com.szmsd.finance.enums;

public enum PrcStateEnum {

    PRC("PRC类型", 1),
    SECOND("二次退费", 2);

    private String desc;
    private Integer code;

    PrcStateEnum(String s,Integer i){
        this.desc = s;
        this.code = i;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
