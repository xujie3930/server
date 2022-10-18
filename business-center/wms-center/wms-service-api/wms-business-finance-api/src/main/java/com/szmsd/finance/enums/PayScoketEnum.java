package com.szmsd.finance.enums;

public enum PayScoketEnum {

    PAY_CONNECT("连接成功", 0),
    PAY_SCAN("支付扫码", 1),
    PAY_ERROR("失败",-1),
    PAY_SUCCESS("成功",2);

    private String desc;
    private Integer index;

    PayScoketEnum(String s,Integer i){
        this.desc = s;
        this.index = i;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
