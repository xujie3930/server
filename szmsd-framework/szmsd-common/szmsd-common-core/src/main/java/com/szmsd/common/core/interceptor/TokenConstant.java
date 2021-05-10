package com.szmsd.common.core.interceptor;

/**
 * @author zhangyuyuan
 * @date 2021-05-10 11:54
 */
public interface TokenConstant {

    /**
     * 前缀
     */
    String PREFIX = "T";

    /**
     * 分隔符
     */
    String SEPARATOR = "-";

    /**
     * token验证类型
     */
    String TOKEN_VERIFICATION = "Token-Verification";

    /**
     * token值
     */
    String TOKEN = "Token";

    /**
     * responseValue
     */
    String R_V = "R-V";

    /**
     * 获取token
     */
    String GET_TOKEN = "GET_TOKEN";

    /**
     * 获取token
     */
    String GET_TOKEN_URL = "/get/token";
}
