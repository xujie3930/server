package com.szmsd.http.util;

import java.util.HashMap;

public class DomainInterceptorUtil {

    public static final String KEYWORD = "sellerCode";
    public static final String REG_EX = "\"\"" + KEYWORD + ":\"(.*?)\"";

    /**
     * 构造一个新的hashMap
     *
     * @param sellerCode 客户code
     * @return 客户 code Map
     */
    public static HashMap<String, String> genSellerCodeHead(String sellerCode) {
        HashMap<String, String> sellerCodeHeadMap = new HashMap<>(1);
        sellerCodeHeadMap.put(KEYWORD, sellerCode);
        return sellerCodeHeadMap;
    }
}
