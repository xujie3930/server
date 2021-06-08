package com.szmsd.bas.api.enums;

import java.util.Objects;

public enum BaseMainEnum {
    SKU_TYPE("059","基础信息-产品属性"),
    SKU_ELE("060","基础信息_带电信息"),
    SKU_ELEPACKAGE("061","基础信息_电池包装"),
    NEW_SKU_REQ("081001","新SKU必验"),
    COLLECT_IN("084002","集运录入"),
    NORMAL_IN("084001","正常录入"),
    ;

    private final String code;
    private final String name;
    BaseMainEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
    public static BaseMainEnum get(String code){
        for(BaseMainEnum anEnum: BaseMainEnum.values()){
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
