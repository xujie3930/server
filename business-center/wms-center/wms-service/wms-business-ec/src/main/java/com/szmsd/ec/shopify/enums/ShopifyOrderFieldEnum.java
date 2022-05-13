package com.szmsd.ec.shopify.enums;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrder
 * @Description: oms_shopify_order shopify订单表FieldEnum
 * @date 2021-04-15 17:39:29
 */
public enum ShopifyOrderFieldEnum {
    
        /**
        * shopify订单id
        */        
    SHOPIFY_ID("shopify_id","shopifyId"),
    
        /**
        * api管理ID
        */        
    ADMIN_GRAPHQL_API_ID("admin_graphql_api_id","adminGraphqlApiId"),
    
                
    APP_ID("app_id","appId"),
    
        /**
        * ip地址
        */        
    BROWSER_IP("browser_ip","browserIp"),
    
        /**
        * 买家是否接受营销
        */        
    BUYER_ACCEPTS_MARKETING("buyer_accepts_marketing","buyerAcceptsMarketing"),
    
        /**
        * 取消原因  customer客户：客户取消了订单, fraud欺诈：订单是欺诈性的,inventory库存：订单中的商品库存不足,declined拒绝：付款被拒绝,other其他
        */        
    CANCEL_REASON("cancel_reason","cancelReason"),
    
        /**
        * 取消订单的日期和时间
        */        
    CANCELLED_AT("cancelled_at","cancelledAt"),
    
        /**
        * 与订单关联的购物车的ID
        */        
    CART_TOKEN("cart_token","cartToken"),
    
        /**
        * 检验id
        */        
    CHECKOUT_ID("checkout_id","checkoutId"),
    
        /**
        * 检验token
        */        
    CHECKOUT_TOKEN("checkout_token","checkoutToken"),
    
        /**
        * 关闭时间
        */        
    CLOSED_AT("closed_at","closedAt"),
    
        /**
        * 是否确认
        */        
    CONFIRMED("confirmed","confirmed"),
    
        /**
        * email联系
        */        
    CONTACT_EMAIL("contact_email","contactEmail"),
    
        /**
        * 订单创建时间
        */        
    CREATED_AT("created_at","createdAt"),
    
        /**
        * 币种
        */        
    CURRENCY("currency","currency"),
    
        /**
        * 小计金额
        */        
    CURRENT_SUBTOTAL_PRICE("current_subtotal_price","currentSubtotalPrice"),
    
        /**
        * 折扣价
        */        
    CURRENT_TOTAL_DISCOUNTS("current_total_discounts","currentTotalDiscounts"),
    
        /**
        * 关税金额
        */        
    CURRENT_TOTAL_DUTIES_SET("current_total_duties_set","currentTotalDutiesSet"),
    
        /**
        * 总金额
        */        
    CURRENT_TOTAL_PRICE("current_total_price","currentTotalPrice"),
    
        /**
        * 税额
        */        
    CURRENT_TOTAL_TAX("current_total_tax","currentTotalTax"),
    
        /**
        * 客户地区
        */        
    CUSTOMER_LOCALE("customer_locale","customerLocale"),
    
        /**
        * 客户设备id
        */        
    DEVICE_ID("device_id","deviceId"),
    
        /**
        * email
        */        
    EMAIL("email","email"),
    
        /**
        * 支付状态：pending待处理,authorized已授权,partially_paid 部分支付,paid已支付,partially_refunded已部分退款,refunded已退款,voided作废
        */        
    FINANCIAL_STATUS("financial_status","financialStatus"),
    
        /**
        * 发货状态：fulfilled已完成,null未发货,partial部分发货,restocked缺货
        */        
    FULFILLMENT_STATUS("fulfillment_status","fulfillmentStatus"),
    
        /**
        * 支付通道：人工
        */        
    GATEWAY("gateway","gateway"),
    
        /**
        * 登录网址
        */        
    LANDING_SITE("landing_site","landingSite"),
    
        /**
        * 登录网址链接
        */        
    LANDING_SITE_REF("landing_site_ref","landingSiteRef"),
    
        /**
        * 地点id
        */        
    LOCATION_ID("location_id","locationId"),
    
        /**
        * 名称
        */        
    NAME("name","name"),
    
        /**
        * 备注
        */        
    NOTE("note","note"),
    
        /**
        * 商品数量
        */        
    NUMBER("number","number"),
    
        /**
        * 订单号
        */        
    ORDER_NUMBER("order_number","orderNumber"),
    
        /**
        * 订单链接
        */        
    ORDER_STATUS_URL("order_status_url","orderStatusUrl"),
    
        /**
        * 原始关税
        */        
    ORIGINAL_TOTAL_DUTIES_SET("original_total_duties_set","originalTotalDutiesSet"),
    
        /**
        * 联系电话
        */        
    PHONE("phone","phone"),
    
        /**
        * 币种
        */        
    PRESENTMENT_CURRENCY("presentment_currency","presentmentCurrency"),
    
        /**
        * 操作时间
        */        
    PROCESSED_AT("processed_at","processedAt"),
    
        /**
        * 操作方式
        */        
    PROCESSING_METHOD("processing_method","processingMethod"),
    
        /**
        * 引用
        */        
    REFERENCE("reference","reference"),
    
        /**
        * 参考网站
        */        
    REFERRING_SITE("referring_site","referringSite"),
    
        /**
        * 源标识符
        */        
    SOURCE_IDENTIFIER("source_identifier","sourceIdentifier"),
    
        /**
        * 来源名字：shopify草稿订单
        */        
    SOURCE_NAME("source_name","sourceName"),
    
        /**
        * 来源地址
        */        
    SOURCE_URL("source_url","sourceUrl"),
    
        /**
        * 小计  = 总价 - 折扣
        */        
    SUBTOTAL_PRICE("subtotal_price","subtotalPrice"),
    
        /**
        * 标签
        */        
    TAGS("tags","tags"),
    
        /**
        * 是否含税
        */        
    TAXES_INCLUDED("taxes_included","taxesIncluded"),
    
        /**
        * 是否是测试单
        */        
    TEST("test","test"),
    
        /**
        * 请求token
        */        
    TOKEN("token","token"),
    
        /**
        * 折扣金额
        */        
    TOTAL_DISCOUNTS("total_discounts","totalDiscounts"),
    
        /**
        * 商品总价
        */        
    TOTAL_LINE_ITEMS_PRICE("total_line_items_price","totalLineItemsPrice"),
    
        /**
        * 未清金额
        */        
    TOTAL_OUTSTANDING("total_outstanding","totalOutstanding"),
    
        /**
        * 总计
        */        
    TOTAL_PRICE("total_price","totalPrice"),
    
        /**
        * 总美金
        */        
    TOTAL_PRICE_USD("total_price_usd","totalPriceUsd"),
    
        /**
        * 税费
        */        
    TOTAL_TAX("total_tax","totalTax"),
    
        /**
        * 总收款
        */        
    TOTAL_TIP_RECEIVED("total_tip_received","totalTipReceived"),
    
        /**
        * 总重
        */        
    TOTAL_WEIGHT("total_weight","totalWeight"),
    
        /**
        * 更新时间
        */        
    UPDATED_AT("updated_at","updatedAt"),
    
        /**
        * shopify用户id
        */        
    USER_ID("user_id","userId"),
    
        /**
        * 账单用户-名
        */        
    BILLING_ADDRESS_FIRST_NAME("billing_address_first_name","billingAddressFirstName"),
    
        /**
        * 账单地址
        */        
    BILLING_ADDRESS_ADDRESS1("billing_address_address1","billingAddressAddress1"),
    
        /**
        * 账单地址-手机
        */        
    BILLING_ADDRESS_PHONE("billing_address_phone","billingAddressPhone"),
    
        /**
        * 账单地址-城市
        */        
    BILLING_ADDRESS_CITY("billing_address_city","billingAddressCity"),
    
        /**
        * 账单地址-邮编
        */        
    BILLING_ADDRESS_ZIP("billing_address_zip","billingAddressZip"),
    
        /**
        * 账单地址-省
        */        
    BILLING_ADDRESS_PROVINCE("billing_address_province","billingAddressProvince"),
    
        /**
        * 账单地址-国家
        */        
    BILLING_ADDRESS_COUNTRY("billing_address_country","billingAddressCountry"),
    
        /**
        * 账单用户-姓
        */        
    BILLING_ADDRESS_LAST_NAME("billing_address_last_name","billingAddressLastName"),
    
        /**
        * 账单地址-地址2
        */        
    BILLING_ADDRESS_ADDRESS2("billing_address_address2","billingAddressAddress2"),
    
        /**
        * 账单地址-公司名称
        */        
    BILLING_ADDRESS_COMPANY("billing_address_company","billingAddressCompany"),
    
        /**
        * 账单地址-纬度
        */        
    BILLING_ADDRESS_LATITUDE("billing_address_latitude","billingAddressLatitude"),
    
        /**
        * 账单地址-经度
        */        
    BILLING_ADDRESS_LONGITUDE("billing_address_longitude","billingAddressLongitude"),
    
        /**
        * 账单名称
        */        
    BILLING_ADDRESS_NAME("billing_address_name","billingAddressName"),
    
        /**
        * 账单地址-国家编号
        */        
    BILLING_ADDRESS_COUNTRY_CODE("billing_address_country_code","billingAddressCountryCode"),
    
        /**
        * 账单地址-省编号
        */        
    BILLING_ADDRESS_PROVINCE_CODE("billing_address_province_code","billingAddressProvinceCode"),
    
        /**
        * 客户-名
        */        
    CUSTOMER_FIRST_NAME("customer_first_name","customerFirstName"),
    
        /**
        * 客户-地址1
        */        
    CUSTOMER_ADDRESS1("customer_address1","customerAddress1"),
    
        /**
        * 客户-手机
        */        
    CUSTOMER_PHONE("customer_phone","customerPhone"),
    
        /**
        * 客户-城市
        */        
    CUSTOMER_CITY("customer_city","customerCity"),
    
        /**
        * 客户-邮编
        */        
    CUSTOMER_ZIP("customer_zip","customerZip"),
    
        /**
        * 客户-省
        */        
    CUSTOMER_PROVINCE("customer_province","customerProvince"),
    
        /**
        * 客户-国家
        */        
    CUSTOMER_COUNTRY("customer_country","customerCountry"),
    
        /**
        * 客户-姓
        */        
    CUSTOMER_LAST_NAME("customer_last_name","customerLastName"),
    
        /**
        * 客户-地址2
        */        
    CUSTOMER_ADDRESS2("customer_address2","customerAddress2"),
    
        /**
        * 客户-公司名称
        */        
    CUSTOMER_COMPANY("customer_company","customerCompany"),
    
        /**
        * 客户-纬度
        */        
    CUSTOMER_LATITUDE("customer_latitude","customerLatitude"),
    
        /**
        * 客户-经度
        */        
    CUSTOMER_LONGITUDE("customer_longitude","customerLongitude"),
    
        /**
        * 客户-姓名
        */        
    CUSTOMER_SHOPIFY_NAME("customer_shopify_name","customerShopifyName"),
    
        /**
        * 客户-国家编号
        */        
    CUSTOMER_COUNTRY_CODE("customer_country_code","customerCountryCode"),
    
        /**
        * 客户-省编号
        */        
    CUSTOMER_PROVINCE_CODE("customer_province_code","customerProvinceCode"),
    
        /**
        * 默认收件人-名
        */        
    DEFAULT_ADDRESS_FIRST_NAME("default_address_first_name","defaultAddressFirstName"),
    
        /**
        * 默认收件人-地址1
        */        
    DEFAULT_ADDRESS_ADDRESS1("default_address_address1","defaultAddressAddress1"),
    
        /**
        * 默认收件人-手机
        */        
    DEFAULT_ADDRESS_PHONE("default_address_phone","defaultAddressPhone"),
    
        /**
        * 默认收件人-城市
        */        
    DEFAULT_ADDRESS_CITY("default_address_city","defaultAddressCity"),
    
        /**
        * 默认收件人-邮编
        */        
    DEFAULT_ADDRESS_ZIP("default_address_zip","defaultAddressZip"),
    
        /**
        * 默认收件人-省
        */        
    DEFAULT_ADDRESS_PROVINCE("default_address_province","defaultAddressProvince"),
    
        /**
        * 默认收件人-国家
        */        
    DEFAULT_ADDRESS_COUNTRY("default_address_country","defaultAddressCountry"),
    
        /**
        * 默认收件人-姓
        */        
    DEFAULT_ADDRESS_LAST_NAME("default_address_last_name","defaultAddressLastName"),
    
        /**
        * 默认收件人-地址2
        */        
    DEFAULT_ADDRESS_ADDRESS2("default_address_address2","defaultAddressAddress2"),
    
        /**
        * 默认收件人-公司名称
        */        
    DEFAULT_ADDRESS_COMPANY("default_address_company","defaultAddressCompany"),
    
        /**
        * 默认收件人-纬度
        */        
    DEFAULT_ADDRESS_LATITUDE("default_address_latitude","defaultAddressLatitude"),
    
        /**
        * 默认收件人-经度
        */        
    DEFAULT_ADDRESS_LONGITUDE("default_address_longitude","defaultAddressLongitude"),
    
        /**
        * 默认收件人-姓名
        */        
    DEFAULT_ADDRESS_NAME("default_address_name","defaultAddressName"),
    
        /**
        * 默认收件人-国家编号
        */        
    DEFAULT_ADDRESS_COUNTRY_CODE("default_address_country_code","defaultAddressCountryCode"),
    
        /**
        * 默认收件人-省编号
        */        
    DEFAULT_ADDRESS_PROVINCE_CODE("default_address_province_code","defaultAddressProvinceCode"),
    
        /**
        * 收货地址-名
        */        
    SHIPPING_ADDRESS_FIRST_NAME("shipping_address_first_name","shippingAddressFirstName"),
    
        /**
        * 收货地址-地址1
        */        
    SHIPPING_ADDRESS_ADDRESS1("shipping_address_address1","shippingAddressAddress1"),
    
        /**
        * 收货地址-手机
        */        
    SHIPPING_ADDRESS_PHONE("shipping_address_phone","shippingAddressPhone"),
    
        /**
        * 收货地址-城市
        */        
    SHIPPING_ADDRESS_CITY("shipping_address_city","shippingAddressCity"),
    
        /**
        * 收货地址-邮编
        */        
    SHIPPING_ADDRESS_ZIP("shipping_address_zip","shippingAddressZip"),
    
        /**
        * 收货地址-省
        */        
    SHIPPING_ADDRESS_PROVINCE("shipping_address_province","shippingAddressProvince"),
    
        /**
        * 收货地址--国家
        */        
    SHIPPING_ADDRESS_COUNTRY("shipping_address_country","shippingAddressCountry"),
    
        /**
        * 收货地址-姓
        */        
    SHIPPING_ADDRESS_LAST_NAME("shipping_address_last_name","shippingAddressLastName"),
    
        /**
        * 收货地址-地址2
        */        
    SHIPPING_ADDRESS_ADDRESS2("shipping_address_address2","shippingAddressAddress2"),
    
        /**
        * 收货地址-公司名称
        */        
    SHIPPING_ADDRESS_COMPANY("shipping_address_company","shippingAddressCompany"),
    
        /**
        * 收货地址-纬度
        */        
    SHIPPING_ADDRESS_LATITUDE("shipping_address_latitude","shippingAddressLatitude"),
    
        /**
        * 收货地址-经度
        */        
    SHIPPING_ADDRESS_LONGITUDE("shipping_address_longitude","shippingAddressLongitude"),
    
        /**
        * 收货地址-姓名
        */        
    SHIPPING_ADDRESS_NAME("shipping_address_name","shippingAddressName"),
    
        /**
        * 收货地址-国家编号
        */        
    SHIPPING_ADDRESS_COUNTRY_CODE("shipping_address_country_code","shippingAddressCountryCode"),
    
        /**
        * 收货地址-省编号
        */        
    SHIPPING_ADDRESS_PROVINCE_CODE("shipping_address_province_code","shippingAddressProvinceCode"),
    
        /**
        * 信用卡编码
        */        
    CREDIT_CARD_BIN("credit_card_bin","creditCardBin"),
    
        /**
        * avs编码
        */        
    AVS_RESULT_CODE("avs_result_code","avsResultCode"),
    
        /**
        * cvv编码
        */        
    CVV_RESULT_CODE("cvv_result_code","cvvResultCode"),
    
        /**
        * 银行
        */        
    CREDIT_CARD_COMPANY("credit_card_company","creditCardCompany"),
    
        /**
        * 银行卡号
        */        
    CREDIT_CARD_NUMBER("credit_card_number","creditCardNumber"),
    
        /**
        * 客户id
        */        
    CUSTOMER_ID("customer_id","customerId"),
    
        /**
        * 客户编号
        */        
    CUSTOMER_CODE("customer_code","customerCode"),
    
        /**
        * 客户名称
        */        
    CUSTOMER_NAME("customer_name","customerName"),
    
        /**
        * 网点编号
        */        
    SITE_CODE("site_code","siteCode"),
    
        /**
        * 店铺id
        */        
    SHOP_ID("shop_id","shopId"),
    
        /**
        * 店铺名称
        */        
    SHOP_NAME("shop_name","shopName"),
    
        /**
        * 承运商id
        */        
    CARRIER_ID("carrier_id","carrierId"),
    
        /**
        * 承运商名称
        */        
    CARRIER_NAME("carrier_name","carrierName"),
    
        /**
        * 服务商编号
        */        
    TRANS_COMP_CODE("trans_comp_code","transCompCode"),
    
        /**
        * 服务商名称
        */        
    TRANS_COMP_NAME("trans_comp_name","transCompName"),
    
        /**
        * 产品代号
        */        
    PRODUCT_CODE("product_code","productCode"),
    
        /**
        * 产品名称
        */        
    PRODUCT_NAME("product_name","productName"),
    
        /**
        * 预推荐仓库名称
        */        
    PRE_WAREHOUSE_NAME("pre_warehouse_name","preWarehouseName"),
    
        /**
        * 配送-id
        */        
    SHIPPING_ID("shipping_id","shippingId"),
    
        /**
        * 配送-承运商
        */        
    SHIPPING_CARRIER_IDENTIFIER("shipping_carrier_identifier","shippingCarrierIdentifier"),
    
        /**
        * 配送-方式编码
        */        
    SHIPPING_CODE("shipping_code","shippingCode"),
    
        /**
        * 配送-交货类别
        */        
    SHIPPING_DELIVERY_CATEGORY("shipping_delivery_category","shippingDeliveryCategory"),
    
        /**
        * 配送-折扣价
        */        
    SHIPPING_DISCOUNTED_PRICE("shipping_discounted_price","shippingDiscountedPrice"),
    
        /**
        * 配送-电话
        */        
    SHIPPING_PHONE("shipping_phone","shippingPhone"),
    
        /**
        * 配送-运费
        */        
    SHIPPING_PRICE("shipping_price","shippingPrice"),
    
        /**
        * 配送-要求配送方式id
        */        
    SHIPPING_REQUESTED_FULFILLMENT_SERVICE_ID("shipping_requested_fulfillment_service_id","shippingRequestedFulfillmentServiceId"),
    
        /**
        * 配送-来源
        */        
    SHIPPING_SOURCE("shipping_source","shippingSource"),
    
        /**
        * 配送-方式
        */        
    SHIPPING_TITLE("shipping_title","shippingTitle"),
    
        /**
        * 当前的订单状态
        */        
    ORDER_STATUS("order_status","orderStatus"),
    
        ;

    /**
     * 数据库字段名
     */
    String dataFieldName;
    
    /**
     * 实体字段名
     */
    String entityFieldName;

    ShopifyOrderFieldEnum(String dataFieldName, String entityFieldName) {
        this.dataFieldName = dataFieldName;
        this.entityFieldName = entityFieldName;
    }
    
    public String getDataFieldName() {
        return dataFieldName;
    }

    public String getEntityFieldName() {
        return entityFieldName;
    }    
}