package com.szmsd.ec.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrderItem
 * @Description: oms_shopify_order_item shopify订单明细表DTO
 * @date 2021-04-15 15:14:13
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class ShopifyOrderItemDTO {
    private static final long serialVersionUID = -39920660999432928L;

    private Long id;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    /**
     * 商品明细id
     */
    @ApiModelProperty(value = "商品明细id")
    private String itemId;
    /**
     * shipify订单表对应的id
     */
    @ApiModelProperty(value = "shipify订单表对应的id")
    private Long shopifyOrderId;
    /**
     * shopify订单id
     */
    @ApiModelProperty(value = "shopify订单id")
    private String shopifyId;
    /**
     * api管理ID
     */
    @ApiModelProperty(value = "api管理ID")
    private String adminGraphqlApiId;
    /**
     * 配送数量
     */
    @ApiModelProperty(value = "配送数量")
    private Integer fulfillableQuantity;
    /**
     * 配送服务方式
     */
    @ApiModelProperty(value = "配送服务方式")
    private String fulfillmentService;
    /**
     * 配送状态
     */
    @ApiModelProperty(value = "配送状态")
    private String fulfillmentStatus;
    /**
     * 礼品卡
     */
    @ApiModelProperty(value = "礼品卡")
    private String giftCard;
    /**
     * 克
     */
    @ApiModelProperty(value = "克")
    private Double grams;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String name;
    /**
     * 商品单价
     */
    @ApiModelProperty(value = "商品单价")
    private Double price;
    /**
     * 产品是否存在
     */
    @ApiModelProperty(value = "产品是否存在")
    private String productExists;
    /**
     * 产品id
     */
    @ApiModelProperty(value = "产品id")
    private Integer productId;
    /**
     * 数量
     */
    @ApiModelProperty(value = "数量")
    private Integer quantity;
    /**
     * 是否需要配送
     */
    @ApiModelProperty(value = "是否需要配送")
    private Double requiresShipping;
    /**
     * sku
     */
    @ApiModelProperty(value = "sku")
    private String sku;
    /**
     * 应纳税
     */
    @ApiModelProperty(value = "应纳税")
    private String taxable;
    /**
     * 商品名称
     */
    @ApiModelProperty(value = "商品名称")
    private String title;
    /**
     * 折扣价
     */
    @ApiModelProperty(value = "折扣价")
    private Double totalDiscount;
    /**
     * 库存id
     */
    @ApiModelProperty(value = "库存id")
    private Double variantId;
    /**
     * 库存管理
     */
    @ApiModelProperty(value = "库存管理")
    private String variantInventoryManagement;
    /**
     * 库存明细
     */
    @ApiModelProperty(value = "库存明细")
    private String variantTitle;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String vendor;
    /**
     * 原始地id
     */
    @ApiModelProperty(value = "原始地id")
    private Double originLocationId;
    /**
     * 原始地-国家编码
     */
    @ApiModelProperty(value = "原始地-国家编码")
    private String originLocationCountryCode;
    /**
     * 原始地-省编码
     */
    @ApiModelProperty(value = "原始地-省编码")
    private String originLocationProvinceCode;
    /**
     * 原始地-名称
     */
    @ApiModelProperty(value = "原始地-名称")
    private String originLocationName;
    /**
     * 原始地-地址1
     */
    @ApiModelProperty(value = "原始地-地址1")
    private String originLocationAddress1;
    /**
     * 原始地-地址2
     */
    @ApiModelProperty(value = "原始地-地址2")
    private String originLocationAddress2;
    /**
     * 原始地-城市
     */
    @ApiModelProperty(value = "原始地-城市")
    private String originLocationCity;
    /**
     * 原始地-邮编
     */
    @ApiModelProperty(value = "原始地-邮编")
    private String originLocationZip;

    /**
     * 承运商id
     */
    @ApiModelProperty(value = "承运商id")
    private Long carrierId;
    /**
     * 承运商名称
     */
    @ApiModelProperty(value = "承运商名称")
    private String carrierName;

    /**
     * 服务商编码
     */
    @ApiModelProperty(value = "服务商编码")
    private String transCompCode;
    /**
     * 服务商名称
     */
    @ApiModelProperty(value = "服务商名称")
    private String transCompName;
    /**
     * 产品代号
     */
    @ApiModelProperty(value = "产品代号")
    private String productCode;
    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;

    /**
     * 拆单号
     */
    @ApiModelProperty(value = "拆单号")
    private String orderCode;
    /**
     * 拆单号
     */
    @ApiModelProperty(value = "运单号")
    private String transferMainNumber;

    @ApiModelProperty(value = "原Sku")
    private String oldSku;

    /**
     * 仓库ID
     */
    @ApiModelProperty(value = "仓库ID")
    private String warehouseId;
    /**
     * 仓库编号
     */
    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;
    /**
     * 仓库名称
     */
    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

}