package com.szmsd.ec.shopify.enums;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrderItem
 * @Description: oms_shopify_order_item shopify订单明细表FieldEnum
 * @date 2021-04-15 17:39:30
 */
public enum ShopifyOrderItemFieldEnum {
    
        /**
        * 商品明细id
        */        
    ITEM_ID("item_id","itemId"),
    
        /**
        * shipify订单表对应的id
        */        
    SHOPIFY_ORDER_ID("shopify_order_id","shopifyOrderId"),
    
        /**
        * shopify订单id
        */        
    SHOPIFY_ID("shopify_id","shopifyId"),
    
        /**
        * api管理ID
        */        
    ADMIN_GRAPHQL_API_ID("admin_graphql_api_id","adminGraphqlApiId"),
    
        /**
        * 配送数量
        */        
    FULFILLABLE_QUANTITY("fulfillable_quantity","fulfillableQuantity"),
    
        /**
        * 配送服务方式
        */        
    FULFILLMENT_SERVICE("fulfillment_service","fulfillmentService"),
    
        /**
        * 配送状态
        */        
    FULFILLMENT_STATUS("fulfillment_status","fulfillmentStatus"),
    
        /**
        * 礼品卡
        */        
    GIFT_CARD("gift_card","giftCard"),
    
        /**
        * 克
        */        
    GRAMS("grams","grams"),
    
        /**
        * 商品名称
        */        
    NAME("name","name"),
    
        /**
        * 商品单价
        */        
    PRICE("price","price"),
    
        /**
        * 产品是否存在
        */        
    PRODUCT_EXISTS("product_exists","productExists"),
    
        /**
        * 产品id
        */        
    PRODUCT_ID("product_id","productId"),
    
        /**
        * 数量
        */        
    QUANTITY("quantity","quantity"),
    
        /**
        * 是否需要配送
        */        
    REQUIRES_SHIPPING("requires_shipping","requiresShipping"),
    
        /**
        * sku
        */        
    SKU("sku","sku"),
    
        /**
        * 应纳税
        */        
    TAXABLE("taxable","taxable"),
    
        /**
        * 商品名称
        */        
    TITLE("title","title"),
    
        /**
        * 折扣价
        */        
    TOTAL_DISCOUNT("total_discount","totalDiscount"),
    
        /**
        * 库存id
        */        
    VARIANT_ID("variant_id","variantId"),
    
        /**
        * 库存管理
        */        
    VARIANT_INVENTORY_MANAGEMENT("variant_inventory_management","variantInventoryManagement"),
    
        /**
        * 库存明细
        */        
    VARIANT_TITLE("variant_title","variantTitle"),
    
        /**
        * 店铺名称
        */        
    VENDOR("vendor","vendor"),
    
        /**
        * 原始地id
        */        
    ORIGIN_LOCATION_ID("origin_location_id","originLocationId"),
    
        /**
        * 原始地-国家编码
        */        
    ORIGIN_LOCATION_COUNTRY_CODE("origin_location_country_code","originLocationCountryCode"),
    
        /**
        * 原始地-省编码
        */        
    ORIGIN_LOCATION_PROVINCE_CODE("origin_location_province_code","originLocationProvinceCode"),
    
        /**
        * 原始地-名称
        */        
    ORIGIN_LOCATION_NAME("origin_location_name","originLocationName"),
    
        /**
        * 原始地-地址1
        */        
    ORIGIN_LOCATION_ADDRESS1("origin_location_address1","originLocationAddress1"),
    
        /**
        * 原始地-地址2
        */        
    ORIGIN_LOCATION_ADDRESS2("origin_location_address2","originLocationAddress2"),
    
        /**
        * 原始地-城市
        */        
    ORIGIN_LOCATION_CITY("origin_location_city","originLocationCity"),
    
        /**
        * 原始地-邮编
        */        
    ORIGIN_LOCATION_ZIP("origin_location_zip","originLocationZip"),
    
        ;

    /**
     * 数据库字段名
     */
    String dataFieldName;
    
    /**
     * 实体字段名
     */
    String entityFieldName;

    ShopifyOrderItemFieldEnum(String dataFieldName, String entityFieldName) {
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