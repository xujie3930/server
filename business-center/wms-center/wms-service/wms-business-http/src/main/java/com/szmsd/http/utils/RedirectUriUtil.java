package com.szmsd.http.utils;

public class RedirectUriUtil {

    public static String APPLICATION_NAME = "wms-business-http";
    public static String REDIRECT_URI_PREFIX = APPLICATION_NAME + ":redirect-uri:";
    public static String ACCESS_TOKEN_PREFIX = APPLICATION_NAME + ":access-token:";
    public static String REFRESH_TOKEN_PREFIX = APPLICATION_NAME + ":refresh-token:";

    public static String wrapRedirectUriKey(String key) {
        return REDIRECT_URI_PREFIX + key;
    }

    public static String wrapAccessTokenKey(String key) {
        return ACCESS_TOKEN_PREFIX + key;
    }

    public static String wrapRefreshTokenKey(String key) {
        return REFRESH_TOKEN_PREFIX + key;
    }
}
