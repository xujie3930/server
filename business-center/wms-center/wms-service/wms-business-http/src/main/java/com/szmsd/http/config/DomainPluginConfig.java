package com.szmsd.http.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = DomainPluginConfig.CONFIG_PREFIX)
public class DomainPluginConfig {
    private final Logger logger = LoggerFactory.getLogger(DomainPluginConfig.class);
    static final String CONFIG_PREFIX = "com.szmsd.domain-plugin";

    private Map<String, List<String>> values;

    public List<String> getPlugins(String domain) {
        if (null == values) {
            return null;
        }
        return this.values.get(domain);
    }
}
