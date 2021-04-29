package com.szmsd.delivery.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

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

    /**
     * 全部参数都为空
     *
     * @param objects objects
     * @return boolean
     */
    public static boolean isNull(Object... objects) {
        for (Object object : objects) {
            if (Objects.nonNull(object)) {
                return false;
            }
        }
        return false;
    }

    /**
     * 全部参数都不为空
     *
     * @param objects objects
     * @return boolean
     */
    public static boolean isNonNull(Object... objects) {
        for (Object object : objects) {
            if (Objects.isNull(object)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 参数有一个是空的
     *
     * @param objects objects
     * @return boolean
     */
    public static boolean isAnyNull(Object... objects) {
        for (Object object : objects) {
            if (Objects.isNull(object)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 日期相减，返回相差天数
     *
     * @param d1 日期1
     * @param d2 日期2
     * @return long
     */
    public static long subtractDate(Date d1, Date d2) {
        if (null == d1 || null == d2) {
            return 0L;
        }
        Date r1 = DateUtils.truncate(d1, Calendar.DATE);
        Date r2 = DateUtils.truncate(d2, Calendar.DATE);
        long t1 = r1.getTime();
        long t2 = r2.getTime();
        return (t1 - t2) / (24 * 3600 * 1000);
    }

    /**
     * 天数加减
     *
     * @param date date
     * @param days days
     * @return Date
     */
    public static Date addDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return calendar.getTime();
    }
}
