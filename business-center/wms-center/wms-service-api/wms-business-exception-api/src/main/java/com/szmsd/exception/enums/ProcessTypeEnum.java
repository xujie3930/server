package com.szmsd.exception.enums;

import java.util.Objects;

public enum ProcessTypeEnum {
    GOONRECEIVING("GoOnReceiving","继续上架"),
    RETURN("Return","退回"),
    DESTROY("Destroy","销毁"),
    GOONSHIPPING("GoOnShipping","继续发货"),
    CANCELORDER("CancelOrder","取消订单"),
    DIRECTLYCOMPLETE("DirectlyComplete","直接完成"),
    ;

    private final String code;
    private final String name;
    ProcessTypeEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
    public static ProcessTypeEnum get(String code){
        for(ProcessTypeEnum anEnum: ProcessTypeEnum.values()){
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
