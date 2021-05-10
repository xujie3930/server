package com.szmsd.common.core.interceptor;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 15:16
 */
public final class EnumUtil {

    /**
     * 获取枚举
     *
     * @param clazz clazz
     * @param str   str
     * @return Enum<?>
     */
    public static Enum<?> valueOf(Class<? extends Enum<?>> clazz, String str) {
        if (clazz.isEnum()) {
            Enum<?>[] enums = clazz.getEnumConstants();
            for (Enum<?> anEnum : enums) {
                if (anEnum.name().equals(str)) {
                    return anEnum;
                }
            }
        }
        return null;
    }
}
