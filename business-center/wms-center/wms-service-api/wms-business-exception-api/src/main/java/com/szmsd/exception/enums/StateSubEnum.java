package com.szmsd.exception.enums;

import java.util.Objects;

/**
 * @FileName StateSubEnum
 * @Description ---------- ---------
 * @Date 2021-06-09
 * @Author jr
 * @Version 1.0
 */
public enum StateSubEnum {
    DAICHULI("085001","待处理"),
    YICHULI("085002","已处理"),
    YIWANCHENG("085003","已完成"),
    ;

    private final String code;
    private final String name;
    StateSubEnum(String code, String name){
        this.code = code;
        this.name = name;
    }
    public static StateSubEnum get(String code){
        for(StateSubEnum anEnum: StateSubEnum.values()){
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
