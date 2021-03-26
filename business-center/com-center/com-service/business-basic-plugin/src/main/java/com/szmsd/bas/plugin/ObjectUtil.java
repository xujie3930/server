package com.szmsd.bas.plugin;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhangyuyuan
 * @date 2020-12-23 023 21:12
 */
public final class ObjectUtil {

    public static final String EMPTY_VALUE = "";
    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    private ObjectUtil() {
    }

    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 转成get方法
     *
     * @param str str，参考格式 USER_NAME
     * @return getUserName
     */
    public static String toGetMethod(String str) {
        str = "_" + str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        sb.append("get");
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 转成get方法
     *
     * @param str str，参考格式userName
     * @return getUserName
     */
    public static String convertToGetMethod(String str) {
        return "get" +
                str.substring(0, 1).toUpperCase() +
                str.substring(1);
    }

    public static String toSetMethod(String str) {
        str = "_" + str.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        sb.append("set");
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String setDefaultEmptyValue(String text) {
        if (Objects.isNull(text)) {
            return EMPTY_VALUE;
        }
        return text;
    }
}
