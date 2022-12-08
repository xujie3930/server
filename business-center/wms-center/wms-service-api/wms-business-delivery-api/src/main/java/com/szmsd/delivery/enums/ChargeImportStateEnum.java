package com.szmsd.delivery.enums;

public enum ChargeImportStateEnum {

    INIT("INIT", "初始化"),
    UPDATE_ORDER("UPDATE_ORDER","更新订单"),
    PRC_ING("PRC_ING", "PRC计费"),
    REFUND_COST("REFUND_COST","退费"),
    FEE_DEDUCTIONS("FEE_DEDUCTIONS","费用扣减"),
    COMPLETED("COMPLETED","完成")
    ;

    private final String code;
    private final String name;

    ChargeImportStateEnum(String code, String name) {
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
