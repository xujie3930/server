package com.szmsd.bas.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = DefaultBasConfig.CONFIG_PREFIX)
public class DefaultBasConfig {
    static final String CONFIG_PREFIX = "com.szmsd.bas";
    private String country;
}
