package com.szmsd.auth.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
@ConfigurationProperties(prefix = ThirdAuthenticationConfig.CONFIG_PREFIX)
public class ThirdAuthenticationConfig {
    public static final String CONFIG_PREFIX = "third.authentication";

    // 多个规则
    private List<DataConfig> values;

    public List<DataConfig> getValues() {
        return values;
    }

    public void setValues(List<DataConfig> values) {
        this.values = values;
    }

    static class DataConfig implements Serializable {
        // 服务名称
        private String applicationName;
        // 接口地址
        private String url;
        // 请求方式
        private HttpMethod httpMethod;
        // 匹配规则
        private String matcher;

        public String getApplicationName() {
            return applicationName;
        }

        public void setApplicationName(String applicationName) {
            this.applicationName = applicationName;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public HttpMethod getHttpMethod() {
            return httpMethod;
        }

        public void setHttpMethod(HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
        }

        public String getMatcher() {
            return matcher;
        }

        public void setMatcher(String matcher) {
            this.matcher = matcher;
        }
    }
}
