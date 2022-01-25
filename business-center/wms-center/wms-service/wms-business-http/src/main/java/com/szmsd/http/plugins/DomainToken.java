package com.szmsd.http.plugins;

public interface DomainToken {

    String getTokenValue();

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
