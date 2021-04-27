package com.szmsd.delivery.util;

import org.apache.commons.lang3.StringUtils;

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

    public static double valueOf(BigDecimal value) {
        if (null == value) {
            return 0D;
        }
        return value.doubleValue();
    }

    public static double defaultValue(Double value) {
        if (null == value) {
            return 0.0;
        }
        return value;
    }

    public static long defaultValue(Long value) {
        if (null == value) {
            return 0L;
        }
        return value;
    }

    public static String defaultValue(String text, String defaultText) {
        if (StringUtils.isEmpty(text)) {
            return defaultText;
        }
        return text;
    }
}
