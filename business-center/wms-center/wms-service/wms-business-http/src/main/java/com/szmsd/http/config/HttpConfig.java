package com.szmsd.http.config;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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

    // ------------------------------------------baseUrl--------------------
    // WMS业务接口 https://wms-open-api.dsloco.com/swagger/index.html
    private String baseUrl;

    // 身份认证
    private String basUserId;
    private String basPassword;

    public Map<String, String> getBaseHeaderMap() {
        Map<String, String> map = new HashMap<>();
        map.put("UserId", this.getBasUserId());
        map.put("Password", this.getBasPassword());
        return map;
    }

    // Inbound
    private InboundConfig inbound;

    // Outbound
    private OutboundConfig outbound;

    private Bas bas;

    @Data
    @Accessors(chain = true)
    public static class InboundConfig {
        // B1 创建入库单
        private String create;
        // B2 取消入库单
        private String cancel;
    }

    @Data
    @Accessors(chain = true)
    public static class Bas {
        // sku和包材
        private String packing;
        // 物料
        private String products;
        //卖家
        private String seller;
    }

    @Data
    @Accessors(chain = true)
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
    }

    // ------------------------------------------pricedProductUrl--------------------
    // 计价系统PRC接口 https://pricedproduct-internalapi-external.dsloco.com/swagger/index.html
    private String pricedProductUrl;

    // 身份认证
    private String pricedProductUserId;
    private String pricedProductPassword;

    // PricedProduct
    private PricedProduct pricedProduct;

    public Map<String, String> getPricedProductHeaderMap() {
        Map<String, String> map = new HashMap<>();
        return map;
    }

    @Data
    @Accessors(chain = true)
    public static class PricedProduct {
        // 根据包裹基本信息获取可下单报价产品
        private String pricedProducts;
    }


    // ------------------------------------------carrierServiceUrl--------------------
    // 服务商接口 https://carrierservice-api-admin-external.dsloco.com/swagger/index.html
    private String carrierServiceUrl;
    // CarrierService
    private CarrierService carrierService;

    @Data
    @Accessors(chain = true)
    public static class CarrierService {
        // 创建承运商物流订单（客户端）
        private String shipmentOrder;
        // 取消承运商物流订单（客户端）
        private String cancellation;
    }
}
