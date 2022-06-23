package com.szmsd.bas.util;

import com.szmsd.bas.config.HMacSHA256;

import java.util.Map;
import java.util.StringJoiner;

public final class ShopifyUtil {

    public static String handlerParameter(Map<String, String[]> parameterMap) {
        StringJoiner joiner = new StringJoiner("&");
        for (String key : parameterMap.keySet()) {
            // ignore hmac
            if ("hmac".equals(key)) {
                continue;
            }
            String text = key + "=";
            String[] valueArr = parameterMap.get(key);
            if (valueArr.length == 1) {
                text += valueArr[0];
            } else {
                StringJoiner valueJoiner = new StringJoiner(", ", "[", "]");
                for (String value : valueArr) {
                    valueJoiner.add(value);
                }
                text += valueJoiner.toString();
            }
            joiner.add(text);
        }
        return joiner.toString();
    }

    public static String encryptParameter(Map<String, String[]> parameterMap, String clientSecret) {
        String parameter = handlerParameter(parameterMap);
        String encryptHex = HMacSHA256.encryptHex(clientSecret, parameter);
        if (null == encryptHex) {
            encryptHex = "-";
        }
        return encryptHex;
    }

    public static String encryptParameterBase64(Map<String, String[]> parameterMap, String clientSecret) {
        String parameter = handlerParameter(parameterMap);
        String encryptBase64 = HMacSHA256.encryptBase64(clientSecret, parameter);
        if (null == encryptBase64) {
            encryptBase64 = "-";
        }
        return encryptBase64;
    }
}
