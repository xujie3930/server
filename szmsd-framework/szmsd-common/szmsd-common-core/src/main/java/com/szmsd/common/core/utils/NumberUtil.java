package com.szmsd.common.core.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * @author liyingfeng
 * @date 2020/8/31 14:11
 */
public class NumberUtil {

    /**
     * 百分号数字
     * @param number
     * @return
     */
    public static String getNumberCent(Object number){
        try {
            NumberFormat perFormat = NumberFormat.getPercentInstance(Locale.CHINA);
            DecimalFormat pf = (DecimalFormat) perFormat;
            pf.setMinimumFractionDigits(2);
            return pf.format(number);
        }catch (Exception e){
            return null;
        }
    }

    /**
     * 文件名称校验
     * @param number
     * @return
     */
    public static boolean checkFileName(String number){
        return number.matches("^\\d{12}$");
    }
}
