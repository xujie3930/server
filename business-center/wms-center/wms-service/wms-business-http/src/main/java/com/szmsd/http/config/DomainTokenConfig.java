package com.szmsd.http.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = DomainTokenConfig.CONFIG_PREFIX)
public class DomainTokenConfig {
    private final Logger logger = LoggerFactory.getLogger(DomainInterceptorConfig.class);
    static final String CONFIG_PREFIX = "com.szmsd.domain-token";

    private Map<String, DomainTokenValue> values;

    public DomainTokenValue getToken(String domain) {
        if (null == values) {
            return null;
        }
        return this.values.get(domain);
    }
}
