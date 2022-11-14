package com.szmsd.common.core.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

    /**
     * 设置小数点,向上取整
     * @param amount
     * @param scale
     * @return
     */
    public static BigDecimal setScale(BigDecimal amount,Integer scale){

        if(amount == null){
            return BigDecimal.ZERO;
        }

        return amount.setScale(scale,BigDecimal.ROUND_UP);
    }

}
