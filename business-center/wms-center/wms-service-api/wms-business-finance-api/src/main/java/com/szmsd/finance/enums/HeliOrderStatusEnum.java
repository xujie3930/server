package com.szmsd.finance.enums;

public enum HeliOrderStatusEnum {

    SUCCESS("成功", 0),
    INIT("未支付", 1),
    FAIL("失败",2),
    CLOSE("关闭",3);

    private String desc;
    private Integer index;

    HeliOrderStatusEnum(String s,Integer i){
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
