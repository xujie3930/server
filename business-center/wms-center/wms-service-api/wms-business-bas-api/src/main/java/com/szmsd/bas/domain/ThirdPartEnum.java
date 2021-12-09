package com.szmsd.bas.domain;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Getter;


@AllArgsConstructor
@Getter
public enum ThirdPartEnum {
    /**
     * DM
     */
    DM(DM.class),
    OMS(DM.class);
    private final Class<?> thirdPartClassType;

    public IThirdPart getInstance(String s) {
        Class<?> thirdPartClassType = this.getThirdPartClassType();
        return (IThirdPart) JSONObject.parseObject(s, thirdPartClassType);
    }

}