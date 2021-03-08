package com.szmsd.open.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 15:01
 */
@Component
@ConfigurationProperties(prefix = AuthConfig.CONFIG_PREFIX)
public class AuthConfig {

    static final String CONFIG_PREFIX = "com.szmsd.open";

    private String userId;

    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
