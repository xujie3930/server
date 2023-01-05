package com.szmsd.http.plugins;

public interface DomainToken {


    String getTokenValue();

    /**
     * 获取本地数据库存储的token
     * @return
     */
    String getLocalTokenValue();

    default String getTokenName() {
        return "Authorization";
    }

    default String accessTokenKey() {
        return null;
    }

    default String refreshTokenKey() {
        return null;
    }
}
