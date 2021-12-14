package com.szmsd.delivery.config;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = Ck1RequestConfig.CONFIG_PREFIX)
public class Ck1RequestConfig {
    private final Logger logger = LoggerFactory.getLogger(Ck1RequestConfig.class);
    static final String CONFIG_PREFIX = "com.szmsd.ck1";

    private Map<String, String> values;
}
