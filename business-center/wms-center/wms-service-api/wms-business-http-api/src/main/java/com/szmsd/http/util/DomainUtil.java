package com.szmsd.http.util;

import com.szmsd.http.enums.DomainEnum;

public final class DomainUtil {

    public static final String PREFIX = "${";
    public static final String SUFFIX = "}";

    /**
     * uri格式${xxx}/api/user
     *
     * @param domainEnum 域名枚举
     * @param api        api
     * @return ${xxx}/api/user
     */
    public static String wrapper(DomainEnum domainEnum, String api) {
        return wrapper(domainEnum.name(), api);
    }

    public static String wrapper(String domain, String api) {
        return PREFIX + domain + SUFFIX + api;
    }

}
