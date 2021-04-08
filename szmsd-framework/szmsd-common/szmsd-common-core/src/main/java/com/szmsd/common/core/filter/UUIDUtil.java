package com.szmsd.common.core.filter;

import java.util.UUID;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 14:28
 */
public final class UUIDUtil {

    //字符库
    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    public static String uuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String suuid() {
        String uuid = uuid();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) { //分为8组
            String str = uuid.substring(i * 4, i * 4 + 4); //每组4位
            int x = Integer.parseInt(str, 16); //输出str在16进制下的表示
            builder.append(chars[x % 0x3E]); //用该16进制数取模62（十六进制表示为314（14即E）），结果作为索引取出字符
        }
        return builder.toString();
    }
}
