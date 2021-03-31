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

    //充值
    private String thirdPayment;
    private String notifyUrl;
    private String rechargeToken;
    // Inbound
    private InboundConfig inbound;
    // Outbound
    private OutboundConfig outbound;
    private Bas bas;
    private ExceptionInfo exceptionInfo;
    // Inventory
    private InventoryInfo inventoryInfo;
    // ------------------------------------------pricedProductUrl--------------------
    // 计价系统PRC接口 https://pricedproduct-internalapi-external.dsloco.com/swagger/index.html
    private String pricedProductUrl;
    // 身份认证
    private String pricedProductUserId;
    private String pricedProductPassword;
    // PricedProduct
    private PricedProduct pricedProduct;
    // PricedProduct
    private PricedSheet pricedSheet;
    // ------------------------------------------carrierServiceUrl--------------------
    // 服务商接口 https://carrierservice-api-admin-external.dsloco.com/swagger/index.html
    private String carrierServiceUrl;
    // CarrierService
    private CarrierService carrierService;

    // 偏远地区接口 https://api-productremotearea-external.dsloco.com/swagger/index.html
    private String productRemoteAreaUrl;

    // RemoteAreaTemplate
    private RemoteAreaTemplate remoteAreaTemplate;

    private ReturnExpressConfig returnExpressConfig;

    public Map<String, String> getBaseHeaderMap() {
        Map<String, String> map = new HashMap<>();
        map.put("UserId", this.getBasUserId());
        map.put("Password", this.getBasPassword());
        return map;
    }

    public Map<String, String> getPricedProductHeaderMap() {
        Map<String, String> map = new HashMap<>();
        return map;
    }

    public Map<String, String> getCarrierServiceHeaderMap() {
        Map<String, String> map = new HashMap<>();
        return map;
    }

    public Map<String, String> getProductRemoteAreaHeaderMap() {
        Map<String, String> map = new HashMap<>();
        return map;
    }

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
        // 物料
        private String packing;
        // sku和包材
        private String products;
        //卖家
        private String seller;
        //特殊操作类型
        private String specialOperationType;
        //特殊操作结果
        private String specialOperationResult;
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

    @Data
    @Accessors(chain = true)
    public static class PricedProduct {
        // 分页查询产品列表，返回指定页面的数据，以及统计总记录数
        private String pageResult;
        // 根据包裹基本信息获取可下单报价产品
        private String pricedProducts;
        // 查询产品下拉列表，返回list数据
        private String keyValuePairs;
        // 创建报价产品信息
        private String products;
        // 导出产品信息列表
        private String exportFile;
        // 计算包裹的费用
        private String pricing;
        // 修改一个计价产品信息的报价表对应的等级和生效时间段
        private String grade;
        // 根据客户代码国家等信息获取可下单产品
        private String inService;
    }

    @Data
    @Accessors(chain = true)
    public static class PricedSheet {
        // 创建报价产品报价表详情信息、修改报价产品报价表详情信息、根据报价表编号获取产品报价表信息
        private String sheets;
        // 使用file文件导入产品报价表信息
        private String importFile;
        // 导出报价表信息
        private String exportFile;
    }

    @Data
    @Accessors(chain = true)
    public static class CarrierService {
        // 创建承运商物流订单（客户端）
        private String shipmentOrder;
        // 取消承运商物流订单（客户端）
        private String cancellation;
        // 获取可用的承运商服务名称(管理端)
        private String services;
    }

    @Data
    @Accessors(chain = true)
    public static class RemoteAreaTemplate {
        // 分页查询地址库模板列表，返回指定页面的数据，以及统计总记录数
        private String pageResult;
        // 导出地址库模板信息
        private String exportFile;
        // 导入地址库模板
        private String importFile;
    }

    @Data
    @Accessors(chain = true)
    public static class ReturnExpressConfig {
        /**
         * 创建退件预报
         * /api/return/expected #F1-VMS 创建退件预报
         */
        private String expectedCreate;

        /**
         * 接收客户提供的处理方式
         * /api/return/processing #F2-VMS 接收客户提供的处理方式
         */
        private String processingUpdate;

    }

    @Data
    @Accessors(chain = true)
    public static class ExceptionInfo {
        // 异常处理
        private String processing;
    }

    @Data
    @Accessors(chain = true)
    public static class InventoryInfo {

        // 创建/修改盘点单
        private String counting;

    }
}
