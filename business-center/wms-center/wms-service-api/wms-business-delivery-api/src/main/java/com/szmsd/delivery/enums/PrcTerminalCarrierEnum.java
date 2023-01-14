package com.szmsd.delivery.enums;

public enum PrcTerminalCarrierEnum {

    CK1("CK1", "出口易订单"),
    CHOUKOU1("CHUKOU1","")
    ;

    private final String code;
    private final String name;

    PrcTerminalCarrierEnum(String code, String name) {
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
