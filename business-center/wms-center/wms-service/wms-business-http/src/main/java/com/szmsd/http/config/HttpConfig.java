package com.szmsd.http.config;

import com.szmsd.http.config.inner.DefaultApiConfig;
import com.szmsd.http.config.inner.UrlGroupConfig;
import com.szmsd.http.enums.HttpUrlType;
import com.szmsd.http.service.http.Utils;
import lombok.Data;
import lombok.experimental.Accessors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
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
    private Logger logger = LoggerFactory.getLogger(HttpConfig.class);
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
    // 多通道请求解析器开关
    private Map<HttpUrlType, Boolean> resolverConfig;

    /**
     * 加载配置之前执行
     */
    public void loadBefore() {
        // 多通道url配置 处理默认值
        if (null == this.multipleChannelUrlSet) {
            this.multipleChannelUrlSet = new HashSet<>();
        }
        // 多通道解析器开关值处理
        if (null == this.resolverConfig) {
            this.resolverConfig = new EnumMap<>(HttpUrlType.class);
        }
    }

    /**
     * 加载配置之后执行
     */
    public void loadAfter() {
        // 对数据进行格式化
        Set<String> copySet = new HashSet<>();
        for (String value : multipleChannelUrlSet) {
            copySet.add(Utils.formatApi(value));
        }
        this.multipleChannelUrlSet = copySet;
        for (HttpUrlType value : HttpUrlType.values()) {
            // 如果不存在，默认配置为true
            this.resolverConfig.putIfAbsent(value, true);
        }
    }

    /**
     * 加载配置异常
     *
     * @param throwable throwable
     */
    public void loadError(Throwable throwable) {
        if (null != throwable) {
            logger.error(throwable.getMessage(), throwable);
        }
    }
}
