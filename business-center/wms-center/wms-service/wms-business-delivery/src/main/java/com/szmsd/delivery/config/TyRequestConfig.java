package com.szmsd.delivery.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = TyRequestConfig.CONFIG_PREFIX)
public class TyRequestConfig {
    private final Logger logger = LoggerFactory.getLogger(TyRequestConfig.class);
    static final String CONFIG_PREFIX = "com.szmsd.ty";

    private Map<String, ApiValue> values;

    /**
     * 获取api
     *
     * @param key key
     * @return ApiValue
     */
    public ApiValue getApi(String key) {
        return values.get(key);
    }
}
