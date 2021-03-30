package com.szmsd.exception.enums;

import java.util.Objects;

public enum OrderTypeEnum {
    RECEIPT("Receipt","入库单"),
    SHIPMENT("Shipment","出库单"),
    ;

    private final String code;
    private final String name;
    OrderTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
    public static OrderTypeEnum get(String code){
        for(OrderTypeEnum anEnum: OrderTypeEnum.values()){
            if(anEnum.getCode().equals(code)){
                return anEnum;
            }
        }
        return null;
    }
    public static boolean has(String code){
        return Objects.nonNull(get(code));
    }
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
