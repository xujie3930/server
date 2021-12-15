package com.szmsd.http.util;

public final class Ck1DomainPluginUtil {

    public static final String PREFIX = "${WarCode:";
    public static final int PREFIX_LENGTH = PREFIX.length();
    public static final String SUFFIX = "}";

    public static String wrapper(String warCode) {
        return PREFIX + warCode + SUFFIX;
    }
}
