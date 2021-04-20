package com.szmsd.http.config;

import com.szmsd.http.config.inner.DefaultApiConfig;
import com.szmsd.http.config.inner.UrlGroupConfig;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 15:01
 */
@Data
@Accessors(chain = true)
@Component
@ConfigurationProperties(prefix = HttpConfig.CONFIG_PREFIX)
public class HttpConfig {

    static final String CONFIG_PREFIX = "com.szmsd.http";

    // 路径组
    private Map<String, UrlGroupConfig> urlGroup;
    // 仓库组
    private Map<String, Set<String>> warehouseGroup;
    // 映射组
    private Map<String, String> mapperGroup;
    // 默认映射组
    private String defaultUrlGroup;
    // 默认api配置
    private DefaultApiConfig defaultApiConfig;

    // 多通道url配置
    private Set<String> multipleChannelUrlSet;

    /**
     * 加载配置之前执行
     */
    public void loadBefore() {
    }

    /**
     * 加载配置之后执行
     */
    public void loadAfter() {
        // 多通道url配置 处理默认值
        if (null == this.multipleChannelUrlSet) {
            this.multipleChannelUrlSet = new HashSet<>();
        }
    }

    /**
     * 加载配置异常
     *
     * @param throwable throwable
     */
    public void loadError(Throwable throwable) {
    }
}
