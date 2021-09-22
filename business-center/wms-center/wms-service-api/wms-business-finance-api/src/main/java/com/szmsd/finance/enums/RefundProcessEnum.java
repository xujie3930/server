package com.szmsd.finance.enums;

import java.util.Arrays;

/**
 * @ClassName: RefundProcessEnum
 * @Description:
 * @Author: 11
 * @Date: 2021-09-10 9:08
 */
public enum RefundProcessEnum {
    /**
     * 加
     */
    ADD,
    /**
     * 减
     */
    SUBTRACT,
    /**
     * 不处理
     */
    NULL;

    public static RefundProcessEnum getProcessStrategy(String name) {
        return Arrays.stream(RefundProcessEnum.values())
                .filter(x -> x.name().equals(name)).findAny().orElse(RefundProcessEnum.NULL);
    }
}
