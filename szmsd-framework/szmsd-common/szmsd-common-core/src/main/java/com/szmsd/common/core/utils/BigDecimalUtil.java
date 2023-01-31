package com.szmsd.common.core.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

    public static final Integer PRICE_SCALE = 2;

    public static final Integer WEIGHT_SCALE = 3;

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

    /**
     * 设置小数点,向上取整
     * @param amount
     * @param scale
     * @return
     */
    public static Double setScale(Double amount,Integer scale){

        if(amount == null){
            return 0D;
        }

        BigDecimal value = new BigDecimal(amount).setScale(scale,BigDecimal.ROUND_UP);

        return value.doubleValue();
    }

    /**
     * 保留小数点，四舍五入
     * @param amount
     * @return
     */
    public static BigDecimal setScale(BigDecimal amount){

        if(amount == null){
            return BigDecimal.ZERO;
        }

        return amount.setScale(2,BigDecimal.ROUND_HALF_UP);
    }

}
