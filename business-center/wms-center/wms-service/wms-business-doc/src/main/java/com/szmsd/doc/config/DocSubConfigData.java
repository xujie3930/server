package com.szmsd.doc.config;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * @ClassName: ConfigData
 * @Description: data配置
 * @Author: 11
 * @Date: 2021-09-11 9:25
 */
@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = DocSubConfigData.CONFIG_PREFIX)
public class DocSubConfigData {
    static final String CONFIG_PREFIX = "com.szmsd.doc.api";

    private MainSubCode mainSubCode;
    private SubCode subCode;

    @Data
    public static class MainSubCode {

        /**
         * 基础信息-产品属性 059
         */
        private String property = "059";

    }
    @Data
    public static class SubCode {

        /**
         * 基础信息-产品属性 059 带电 059004
         */
        private String charged = "059004";

    }
    @PostConstruct
    private void afterInit() {
        log.info(JSONObject.toJSONString(this));
    }
}
