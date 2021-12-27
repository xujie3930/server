package com.szmsd.http.plugins;

import java.util.Map;

public interface DomainInterceptor {

    default boolean preHandle(String uri, Map<String, String> requestHeaders, String requestBody) {
        return true;
    }

    default void postHandle(String uri, Object responseBody) {

    }

    default void afterCompletion(String uri, Object responseBody) {

    }
}
