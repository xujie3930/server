package com.szmsd.http.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = DomainConfig.CONFIG_PREFIX)
public class DomainConfig {
    private final Logger logger = LoggerFactory.getLogger(DomainHeaderConfig.class);
    static final String CONFIG_PREFIX = "com.szmsd.domain";

    private Map<String, String> values;

    /**
     * 获取域名
     *
     * @param key key
     * @return String
     */
    public String getDomain(String key) {
        if (null == values) {
            return null;
        }
        return values.get(key);
    }
}
