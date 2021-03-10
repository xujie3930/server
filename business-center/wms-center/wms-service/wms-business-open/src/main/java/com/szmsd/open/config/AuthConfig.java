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

    private String appId;

    private String sign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
