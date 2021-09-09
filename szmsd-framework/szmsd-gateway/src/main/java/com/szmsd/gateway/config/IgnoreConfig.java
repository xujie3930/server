package com.szmsd.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@ConfigurationProperties(prefix = IgnoreConfig.CONFIG_PREFIX)
public class IgnoreConfig {

    static final String CONFIG_PREFIX = "com.szmsd.ignore";

    private Set<String> urls;
    private Set<String> matchUrls;

    public Set<String> getUrls() {
        return urls;
    }

    public void setUrls(Set<String> urls) {
        this.urls = urls;
    }

    public Set<String> getMatchUrls() {
        return matchUrls;
    }

    public void setMatchUrls(Set<String> matchUrls) {
        this.matchUrls = matchUrls;
    }
}
