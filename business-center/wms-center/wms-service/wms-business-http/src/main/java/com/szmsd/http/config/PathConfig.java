package com.szmsd.http.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-05-07 9:44
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = PathConfig.CONFIG_PREFIX)
public class PathConfig {
    static final String CONFIG_PREFIX = "com.szmsd.path";

    /**
     * 由Servlet处理的请求路径
     */
    private Set<String> paths;
}
