package com.szmsd.doc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-07-28 9:51
 */
// @Data
// @Configuration
// @ConfigurationProperties(prefix = DocOauthConfig.CONFIG_PREFIX)
public class DocOauthConfig {

    static final String CONFIG_PREFIX = "com.szmsd.doc.oauth";

    private List<UserConfig> users;
}
