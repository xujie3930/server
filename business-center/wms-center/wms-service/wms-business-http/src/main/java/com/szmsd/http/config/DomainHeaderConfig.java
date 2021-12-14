package com.szmsd.http.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = DomainHeaderConfig.CONFIG_PREFIX)
public class DomainHeaderConfig {
    private final Logger logger = LoggerFactory.getLogger(DomainHeaderConfig.class);
    static final String CONFIG_PREFIX = "com.szmsd.domain-header";

    private Map<String, Map<String, String>> values;

    public Map<String, String> getHeaders(String uri) {
        if (null == values) {
            return null;
        }
        return values.get(DomainUtil.getDomain(uri));
    }

}
