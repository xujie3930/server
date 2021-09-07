package com.szmsd.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = DocCodeAuthConfiguration.PREFIX)
public class DocCodeAuthConfiguration {

    public static final String PREFIX = "com.szmsd.auth.doc";

    private String locationUrl;

    private String authenticationUrl;

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public String getAuthenticationUrl() {
        return authenticationUrl;
    }

    public void setAuthenticationUrl(String authenticationUrl) {
        this.authenticationUrl = authenticationUrl;
    }
}
