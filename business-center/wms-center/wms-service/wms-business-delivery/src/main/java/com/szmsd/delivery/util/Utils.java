package com.szmsd.delivery.util;

import java.math.BigDecimal;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 15:00
 */
public final class Utils {

    private Utils() {
    }

    public static int valueOfDouble(Double value) {
        if (null == value) {
            return 0;
        }
        return value.intValue();
    }

    public static int valueOfLong(Long value) {
        if (null == value) {
            return 0;
        }
        return value.intValue();
    }

    public static BigDecimal valueOf(Double value) {
        if (null == value) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(value);
    }
}
