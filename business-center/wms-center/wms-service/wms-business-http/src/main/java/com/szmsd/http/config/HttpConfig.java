package com.szmsd.http.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangyuyuan
 * @date 2021-03-06 15:01
 */
@Component
@ConfigurationProperties(prefix = HttpConfig.CONFIG_PREFIX)
public class HttpConfig {

    static final String CONFIG_PREFIX = "com.szmsd.http";

    // 身份认证
    private String userId;
    private String password;

    // 根目录
    private String baseUrl;

    // Outbound
    private OutboundConfig outbound;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public OutboundConfig getOutbound() {
        return outbound;
    }

    public void setOutbound(OutboundConfig outbound) {
        this.outbound = outbound;
    }

    public Map<String, String> getHeaderMap() {
        Map<String, String> map = new HashMap<>();
        map.put("UserId", this.getUserId());
        map.put("Password", this.getPassword());
        return map;
    }

    public static class OutboundConfig {
        // C1 创建出库单
        private String create;
        // C2 取消出库单
        private String cancel;
        // C3 更新出库单挂号
        private String tracking;
        // C4 更新出库单标签
        private String label;
        // D2 更新出库单发货指令
        private String shipping;

        public String getCreate() {
            return create;
        }

        public void setCreate(String create) {
            this.create = create;
        }

        public String getCancel() {
            return cancel;
        }

        public void setCancel(String cancel) {
            this.cancel = cancel;
        }

        public String getTracking() {
            return tracking;
        }

        public void setTracking(String tracking) {
            this.tracking = tracking;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getShipping() {
            return shipping;
        }

        public void setShipping(String shipping) {
            this.shipping = shipping;
        }
    }
}
