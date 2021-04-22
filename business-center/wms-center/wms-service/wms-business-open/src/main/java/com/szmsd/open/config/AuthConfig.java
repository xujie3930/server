package com.szmsd.open.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 15:01
 */
@Component
@ConfigurationProperties(prefix = AuthConfig.CONFIG_PREFIX)
public class AuthConfig {

    static final String CONFIG_PREFIX = "com.szmsd.open";

    private Set<String> whiteSet;

    private Map<String, String> accountMap;

    public Set<String> getWhiteSet() {
        return whiteSet;
    }

    public void setWhiteSet(Set<String> whiteSet) {
        this.whiteSet = whiteSet;
    }

    public Map<String, String> getAccountMap() {
        return accountMap;
    }

    public void setAccountMap(Map<String, String> accountMap) {
        this.accountMap = accountMap;
    }
}
