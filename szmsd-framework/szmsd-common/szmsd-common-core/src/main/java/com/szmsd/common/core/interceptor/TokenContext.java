package com.szmsd.common.core.interceptor;

/**
 * @author zhangyuyuan
 * @date 2021-05-11 10:21
 */
public class TokenContext {
    private static final ThreadLocal<TokenContext> contextHolder = new ThreadLocal<>();

    private String onlyKey;

    private TokenContext() {
    }

    public static synchronized TokenContext get() {
        TokenContext context = contextHolder.get();
        if (context == null) {
            contextHolder.set(new TokenContext());
            context = contextHolder.get();
        }
        return context;
    }

    public static void destroy() {
        contextHolder.remove();
    }

    public String getOnlyKey() {
        return onlyKey;
    }

    public void setOnlyKey(String onlyKey) {
        this.onlyKey = onlyKey;
    }
}
