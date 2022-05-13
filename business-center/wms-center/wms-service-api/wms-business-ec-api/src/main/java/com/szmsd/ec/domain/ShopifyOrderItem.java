package com.szmsd.ec.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrderItem
 * @Description: oms_shopify_order_item shopify订单明细表实体类
 * @date 2021-04-15 15:14:10
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@TableName("ec_shopify_order_item")
public class ShopifyOrderItem extends BaseEntity {
    private static final long serialVersionUID = 410981536992975126L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 商品明细id
     */
    @ApiModelProperty(value = "商品明细id")
    @TableField(value = "item_id")
    private String itemId;
    /**
     * shipify订单表对应的id
     */
    @ApiModelProperty(value = "shipify订单表对应的id")
    @TableField(value = "shopify_order_id")
    private Long shopifyOrderId;
    /**
     * shopify订单id
     */
    @ApiModelProperty(value = "shopify订单id")
    @TableField(value = "shopify_id")
    private String shopifyId;
    /**
     * api管理ID
     */
    @ApiModelProperty(value = "api管理ID")
    @TableField(value = "admin_graphql_api_id")
    private String adminGraphqlApiId;
    /**
     * 配送数量
     */
    @ApiModelProperty(value = "配送数量")
    @TableField(value = "fulfillable_quantity")
    private Integer fulfillableQuantity;
    /**
     * 配送服务方式
     */
    @ApiModelProperty(value = "配送服务方式")
    @TableField(value = "fulfillment_service")
    private String fulfillmentService;
    /**
     * 配送状态
     */
    @ApiModelProperty(value = "配送状态")
    @TableField(value = "fulfillment_status")
    private String fulfillmentStatus;
    /**
     * 礼品卡
     */
    @ApiModelProperty(value = "礼品卡")
    @TableField(value = "gift_card")
    private String giftCard;
    /**
     * 克
     */
    @ApiModelProperty(value = "克")
    @TableField(value = "grams")
    private Double grams;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    @TableField(value = "name")
    private String name;
    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价")
    @TableField(value = "price")
    private Double price;
    /**
     * 产品是否存在
     */
    @ApiModelProperty(value = "产品是否存在")
    @TableField(value = "product_exists")
    private String productExists;
    /**
     * 产品id
     */
    @ApiModelProperty(value = "产品id")
    @TableField(value = "product_id")
    private Integer productId;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    @TableField(value = "quantity")
    private Integer quantity;
    /**
     * 是否需要配送
     */
    @ApiModelProperty(value = "是否需要配送")
    @TableField(value = "requires_shipping")
    private Double requiresShipping;
    /**
     * sku
     */
    @ApiModelProperty(value = "sku")
    @TableField(value = "sku")
    private String sku;
    /**
     * 应纳税
     */
    @ApiModelProperty(value = "应纳税")
    @TableField(value = "taxable")
    private String taxable;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    @TableField(value = "title")
    private String title;
    /**
     * 折扣价
     */
    @ApiModelProperty(value = "折扣价")
    @TableField(value = "total_discount")
    private Double totalDiscount;
    /**
     * 库存id
     */
    @ApiModelProperty(value = "库存id")
    @TableField(value = "variant_id")
    private Double variantId;
    /**
     * 库存管理
     */
    @ApiModelProperty(value = "库存管理")
    @TableField(value = "variant_inventory_management")
    private String variantInventoryManagement;
    /**
     * 库存明细
     */
    @ApiModelProperty(value = "库存明细")
    @TableField(value = "variant_title")
    private String variantTitle;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    @TableField(value = "vendor")
    private String vendor;
    /**
     * 原始地id
     */
    @ApiModelProperty(value = "原始地id")
    @TableField(value = "origin_location_id")
    private Double originLocationId;
    /**
     * 原始地-国家编码
     */
    @ApiModelProperty(value = "原始地-国家编码")
    @TableField(value = "origin_location_country_code")
    private String originLocationCountryCode;
    /**
     * 原始地-省编码
     */
    @ApiModelProperty(value = "原始地-省编码")
    @TableField(value = "origin_location_province_code")
    private String originLocationProvinceCode;
    /**
     * 原始地-名称
     */
    @ApiModelProperty(value = "原始地-名称")
    @TableField(value = "origin_location_name")
    private String originLocationName;
    /**
     * 原始地-地址1
     */
    @ApiModelProperty(value = "原始地-地址1")
    @TableField(value = "origin_location_address1")
    private String originLocationAddress1;
    /**
     * 原始地-地址2
     */
    @ApiModelProperty(value = "原始地-地址2")
    @TableField(value = "origin_location_address2")
    private String originLocationAddress2;
    /**
     * 原始地-城市
     */
    @ApiModelProperty(value = "原始地-城市")
    @TableField(value = "origin_location_city")
    private String originLocationCity;
    /**
     * 原始地-邮编
     */
    @ApiModelProperty(value = "原始地-邮编")
    @TableField(value = "origin_location_zip")
    private String originLocationZip;
    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID")
    @TableField(value = "warehouse_id")
    private String warehouseId;
    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    @TableField(value = "warehouse_name")
    private String warehouseName;

}