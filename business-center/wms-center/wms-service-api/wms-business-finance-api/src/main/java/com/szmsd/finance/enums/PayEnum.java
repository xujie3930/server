package com.szmsd.finance.enums;

public enum PayEnum {

    ALIPAYSCAN("支付宝主扫", 6),
    WXPAYSCAN("微信主扫", 7);

    private String desc;
    private Integer index;

    PayEnum(String s,Integer i){
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
