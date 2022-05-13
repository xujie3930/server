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

import java.util.Date;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrder
 * @Description: oms_shopify_order shopify订单表实体类
 * @date 2021-04-15 15:14:08
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@TableName("ec_shopify_order")
public class ShopifyOrder extends BaseEntity {
    private static final long serialVersionUID = -59008944305453499L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
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

    @ApiModelProperty(value = "${column.comment}")
    @TableField(value = "app_id")
    private String appId;
    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    @TableField(value = "browser_ip")
    private String browserIp;
    /**
     * 买家是否接受营销
     */
    @ApiModelProperty(value = "买家是否接受营销")
    @TableField(value = "buyer_accepts_marketing")
    private String buyerAcceptsMarketing;
    /**
     * 取消原因  customer客户：客户取消了订单, fraud欺诈：订单是欺诈性的,inventory库存：订单中的商品库存不足,declined拒绝：付款被拒绝,other其他
     */
    @ApiModelProperty(value = "取消原因  customer客户：客户取消了订单, fraud欺诈：订单是欺诈性的,inventory库存：订单中的商品库存不足,declined拒绝：付款被拒绝,other其他")
    @TableField(value = "cancel_reason")
    private String cancelReason;
    /**
     * 取消订单的日期和时间
     */
    @ApiModelProperty(value = "取消订单的日期和时间")
    @TableField(value = "cancelled_at")
    private Date cancelledAt;
    /**
     * 与订单关联的购物车的ID
     */
    @ApiModelProperty(value = "与订单关联的购物车的ID")
    @TableField(value = "cart_token")
    private String cartToken;
    /**
     * 检验id
     */
    @ApiModelProperty(value = "检验id")
    @TableField(value = "checkout_id")
    private String checkoutId;
    /**
     * 检验token
     */
    @ApiModelProperty(value = "检验token")
    @TableField(value = "checkout_token")
    private String checkoutToken;
    /**
     * 关闭时间
     */
    @ApiModelProperty(value = "关闭时间")
    @TableField(value = "closed_at")
    private Date closedAt;
    /**
     * 是否确认
     */
    @ApiModelProperty(value = "是否确认")
    @TableField(value = "confirmed")
    private String confirmed;
    /**
     * email联系
     */
    @ApiModelProperty(value = "email联系")
    @TableField(value = "contact_email")
    private String contactEmail;
    /**
     * 订单创建时间
     */
    @ApiModelProperty(value = "订单创建时间")
    @TableField(value = "created_at")
    private Date createdAt;
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    @TableField(value = "currency")
    private String currency;
    /**
     * 小计金额
     */
    @ApiModelProperty(value = "小计金额")
    @TableField(value = "current_subtotal_price")
    private Double currentSubtotalPrice;
    /**
     * 折扣价
     */
    @ApiModelProperty(value = "折扣价")
    @TableField(value = "current_total_discounts")
    private Double currentTotalDiscounts;
    /**
     * 关税金额
     */
    @ApiModelProperty(value = "关税金额")
    @TableField(value = "current_total_duties_set")
    private Double currentTotalDutiesSet;
    /**
     * 总金额
     */
    @ApiModelProperty(value = "总金额")
    @TableField(value = "current_total_price")
    private Double currentTotalPrice;
    /**
     * 税额
     */
    @ApiModelProperty(value = "税额")
    @TableField(value = "current_total_tax")
    private Double currentTotalTax;
    /**
     * 客户地区
     */
    @ApiModelProperty(value = "客户地区")
    @TableField(value = "customer_locale")
    private String customerLocale;
    /**
     * 客户设备id
     */
    @ApiModelProperty(value = "客户设备id")
    @TableField(value = "device_id")
    private String deviceId;
    /**
     * email
     */
    @ApiModelProperty(value = "email")
    @TableField(value = "email")
    private String email;
    /**
     * 支付状态：pending待处理,authorized已授权,partially_paid 部分支付,paid已支付,partially_refunded已部分退款,refunded已退款,voided作废
     */
    @ApiModelProperty(value = "支付状态：pending待处理,authorized已授权,partially_paid 部分支付,paid已支付,partially_refunded已部分退款,refunded已退款,voided作废")
    @TableField(value = "financial_status")
    private String financialStatus;
    /**
     * 发货状态：fulfilled已完成,null未发货,partial部分发货,restocked缺货
     */
    @ApiModelProperty(value = "发货状态：fulfilled已完成,null未发货,partial部分发货,restocked缺货")
    @TableField(value = "fulfillment_status")
    private String fulfillmentStatus;
    /**
     * 支付通道：人工
     */
    @ApiModelProperty(value = "支付通道：人工")
    @TableField(value = "gateway")
    private String gateway;
    /**
     * 登录网址
     */
    @ApiModelProperty(value = "登录网址")
    @TableField(value = "landing_site")
    private String landingSite;
    /**
     * 登录网址链接
     */
    @ApiModelProperty(value = "登录网址链接")
    @TableField(value = "landing_site_ref")
    private String landingSiteRef;
    /**
     * 地点id
     */
    @ApiModelProperty(value = "地点id")
    @TableField(value = "location_id")
    private String locationId;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    @TableField(value = "name")
    private String name;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    @TableField(value = "note")
    private String note;
    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品数量")
    @TableField(value = "number")
    private Double number;
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    @TableField(value = "order_number")
    private Integer orderNumber;;
    /**
     * 订单链接
     */
    @ApiModelProperty(value = "订单链接")
    @TableField(value = "order_status_url")
    private String orderStatusUrl;
    /**
     * 原始关税
     */
    @ApiModelProperty(value = "原始关税")
    @TableField(value = "original_total_duties_set")
    private String originalTotalDutiesSet;
    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    @TableField(value = "phone")
    private String phone;
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    @TableField(value = "presentment_currency")
    private String presentmentCurrency;
    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    @TableField(value = "processed_at")
    private Date processedAt;
    /**
     * 操作方式
     */
    @ApiModelProperty(value = "操作方式")
    @TableField(value = "processing_method")
    private String processingMethod;
    /**
     * 引用
     */
    @ApiModelProperty(value = "引用")
    @TableField(value = "reference")
    private String reference;
    /**
     * 参考网站
     */
    @ApiModelProperty(value = "参考网站")
    @TableField(value = "referring_site")
    private String referringSite;
    /**
     * 源标识符
     */
    @ApiModelProperty(value = "源标识符")
    @TableField(value = "source_identifier")
    private String sourceIdentifier;
    /**
     * 来源名字：shopify草稿订单
     */
    @ApiModelProperty(value = "来源名字：shopify草稿订单")
    @TableField(value = "source_name")
    private String sourceName;
    /**
     * 来源地址
     */
    @ApiModelProperty(value = "来源地址")
    @TableField(value = "source_url")
    private String sourceUrl;
    /**
     * 小计  = 总价 - 折扣
     */
    @ApiModelProperty(value = "小计  = 总价 - 折扣")
    @TableField(value = "subtotal_price")
    private Double subtotalPrice;
    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    @TableField(value = "tags")
    private String tags;
    /**
     * 是否含税
     */
    @ApiModelProperty(value = "是否含税")
    @TableField(value = "taxes_included")
    private String taxesIncluded;
    /**
     * 是否是测试单
     */
    @ApiModelProperty(value = "是否是测试单")
    @TableField(value = "test")
    private String test;
    /**
     * 请求token
     */
    @ApiModelProperty(value = "请求token")
    @TableField(value = "token")
    private String token;
    /**
     * 折扣金额
     */
    @ApiModelProperty(value = "折扣金额")
    @TableField(value = "total_discounts")
    private Double totalDiscounts;
    /**
     * 商品总价
     */
    @ApiModelProperty(value = "商品总价")
    @TableField(value = "total_line_items_price")
    private Double totalLineItemsPrice;
    /**
     * 未清金额
     */
    @ApiModelProperty(value = "未清金额")
    @TableField(value = "total_outstanding")
    private Double totalOutstanding;
    /**
     * 总计
     */
    @ApiModelProperty(value = "总计")
    @TableField(value = "total_price")
    private Double totalPrice;
    /**
     * 总美金
     */
    @ApiModelProperty(value = "总美金")
    @TableField(value = "total_price_usd")
    private Double totalPriceUsd;
    /**
     * 税费
     */
    @ApiModelProperty(value = "税费")
    @TableField(value = "total_tax")
    private Double totalTax;
    /**
     * 总收款
     */
    @ApiModelProperty(value = "总收款")
    @TableField(value = "total_tip_received")
    private Double totalTipReceived;
    /**
     * 总重
     */
    @ApiModelProperty(value = "总重")
    @TableField(value = "total_weight")
    private Double totalWeight;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    @TableField(value = "updated_at")
    private Date updatedAt;
    /**
     * shopify用户id
     */
    @ApiModelProperty(value = "shopify用户id")
    @TableField(value = "user_id")
    private Integer userId;
    /**
     * 账单用户-名
     */
    @ApiModelProperty(value = "账单用户-名")
    @TableField(value = "billing_address_first_name")
    private String billingAddressFirstName;
    /**
     * 账单地址
     */
    @ApiModelProperty(value = "账单地址")
    @TableField(value = "billing_address_address1")
    private String billingAddressAddress1;
    /**
     * 账单地址-手机
     */
    @ApiModelProperty(value = "账单地址-手机")
    @TableField(value = "billing_address_phone")
    private String billingAddressPhone;
    /**
     * 账单地址-城市
     */
    @ApiModelProperty(value = "账单地址-城市")
    @TableField(value = "billing_address_city")
    private String billingAddressCity;
    /**
     * 账单地址-邮编
     */
    @ApiModelProperty(value = "账单地址-邮编")
    @TableField(value = "billing_address_zip")
    private String billingAddressZip;
    /**
     * 账单地址-省
     */
    @ApiModelProperty(value = "账单地址-省")
    @TableField(value = "billing_address_province")
    private String billingAddressProvince;
    /**
     * 账单地址-国家
     */
    @ApiModelProperty(value = "账单地址-国家")
    @TableField(value = "billing_address_country")
    private String billingAddressCountry;
    /**
     * 账单用户-姓
     */
    @ApiModelProperty(value = "账单用户-姓")
    @TableField(value = "billing_address_last_name")
    private String billingAddressLastName;
    /**
     * 账单地址-地址2
     */
    @ApiModelProperty(value = "账单地址-地址2")
    @TableField(value = "billing_address_address2")
    private String billingAddressAddress2;
    /**
     * 账单地址-公司名称
     */
    @ApiModelProperty(value = "账单地址-公司名称")
    @TableField(value = "billing_address_company")
    private String billingAddressCompany;
    /**
     * 账单地址-纬度
     */
    @ApiModelProperty(value = "账单地址-纬度")
    @TableField(value = "billing_address_latitude")
    private Double billingAddressLatitude;
    /**
     * 账单地址-经度
     */
    @ApiModelProperty(value = "账单地址-经度")
    @TableField(value = "billing_address_longitude")
    private Double billingAddressLongitude;
    /**
     * 账单名称
     */
    @ApiModelProperty(value = "账单名称")
    @TableField(value = "billing_address_name")
    private String billingAddressName;
    /**
     * 账单地址-国家编号
     */
    @ApiModelProperty(value = "账单地址-国家编号")
    @TableField(value = "billing_address_country_code")
    private String billingAddressCountryCode;
    /**
     * 账单地址-省编号
     */
    @ApiModelProperty(value = "账单地址-省编号")
    @TableField(value = "billing_address_province_code")
    private String billingAddressProvinceCode;
    /**
     * 客户-名
     */
    @ApiModelProperty(value = "客户-名")
    @TableField(value = "customer_first_name")
    private String customerFirstName;
    /**
     * 客户-地址1
     */
    @ApiModelProperty(value = "客户-地址1")
    @TableField(value = "customer_address1")
    private String customerAddress1;
    /**
     * 客户-手机
     */
    @ApiModelProperty(value = "客户-手机")
    @TableField(value = "customer_phone")
    private String customerPhone;
    /**
     * 客户-城市
     */
    @ApiModelProperty(value = "客户-城市")
    @TableField(value = "customer_city")
    private String customerCity;
    /**
     * 客户-邮编
     */
    @ApiModelProperty(value = "客户-邮编")
    @TableField(value = "customer_zip")
    private String customerZip;
    /**
     * 客户-省
     */
    @ApiModelProperty(value = "客户-省")
    @TableField(value = "customer_province")
    private String customerProvince;
    /**
     * 客户-国家
     */
    @ApiModelProperty(value = "客户-国家")
    @TableField(value = "customer_country")
    private String customerCountry;
    /**
     * 客户-姓
     */
    @ApiModelProperty(value = "客户-姓")
    @TableField(value = "customer_last_name")
    private String customerLastName;
    /**
     * 客户-地址2
     */
    @ApiModelProperty(value = "客户-地址2")
    @TableField(value = "customer_address2")
    private String customerAddress2;
    /**
     * 客户-公司名称
     */
    @ApiModelProperty(value = "客户-公司名称")
    @TableField(value = "customer_company")
    private String customerCompany;
    /**
     * 客户-纬度
     */
    @ApiModelProperty(value = "客户-纬度")
    @TableField(value = "customer_latitude")
    private Double customerLatitude;
    /**
     * 客户-经度
     */
    @ApiModelProperty(value = "客户-经度")
    @TableField(value = "customer_longitude")
    private Double customerLongitude;
    /**
     * 客户-姓名
     */
    @ApiModelProperty(value = "客户-姓名")
    @TableField(value = "customer_shopify_name")
    private String customerShopifyName;
    /**
     * 客户-国家编号
     */
    @ApiModelProperty(value = "客户-国家编号")
    @TableField(value = "customer_country_code")
    private String customerCountryCode;
    /**
     * 客户-省编号
     */
    @ApiModelProperty(value = "客户-省编号")
    @TableField(value = "customer_province_code")
    private String customerProvinceCode;
    /**
     * 默认收件人-名
     */
    @ApiModelProperty(value = "默认收件人-名")
    @TableField(value = "default_address_first_name")
    private String defaultAddressFirstName;
    /**
     * 默认收件人-地址1
     */
    @ApiModelProperty(value = "默认收件人-地址1")
    @TableField(value = "default_address_address1")
    private String defaultAddressAddress1;
    /**
     * 默认收件人-手机
     */
    @ApiModelProperty(value = "默认收件人-手机")
    @TableField(value = "default_address_phone")
    private String defaultAddressPhone;
    /**
     * 默认收件人-城市
     */
    @ApiModelProperty(value = "默认收件人-城市")
    @TableField(value = "default_address_city")
    private String defaultAddressCity;
    /**
     * 默认收件人-邮编
     */
    @ApiModelProperty(value = "默认收件人-邮编")
    @TableField(value = "default_address_zip")
    private String defaultAddressZip;
    /**
     * 默认收件人-省
     */
    @ApiModelProperty(value = "默认收件人-省")
    @TableField(value = "default_address_province")
    private String defaultAddressProvince;
    /**
     * 默认收件人-国家
     */
    @ApiModelProperty(value = "默认收件人-国家")
    @TableField(value = "default_address_country")
    private String defaultAddressCountry;
    /**
     * 默认收件人-姓
     */
    @ApiModelProperty(value = "默认收件人-姓")
    @TableField(value = "default_address_last_name")
    private String defaultAddressLastName;
    /**
     * 默认收件人-地址2
     */
    @ApiModelProperty(value = "默认收件人-地址2")
    @TableField(value = "default_address_address2")
    private String defaultAddressAddress2;
    /**
     * 默认收件人-公司名称
     */
    @ApiModelProperty(value = "默认收件人-公司名称")
    @TableField(value = "default_address_company")
    private String defaultAddressCompany;
    /**
     * 默认收件人-纬度
     */
    @ApiModelProperty(value = "默认收件人-纬度")
    @TableField(value = "default_address_latitude")
    private Double defaultAddressLatitude;
    /**
     * 默认收件人-经度
     */
    @ApiModelProperty(value = "默认收件人-经度")
    @TableField(value = "default_address_longitude")
    private Double defaultAddressLongitude;
    /**
     * 默认收件人-姓名
     */
    @ApiModelProperty(value = "默认收件人-姓名")
    @TableField(value = "default_address_name")
    private String defaultAddressName;
    /**
     * 默认收件人-国家编号
     */
    @ApiModelProperty(value = "默认收件人-国家编号")
    @TableField(value = "default_address_country_code")
    private String defaultAddressCountryCode;
    /**
     * 默认收件人-省编号
     */
    @ApiModelProperty(value = "默认收件人-省编号")
    @TableField(value = "default_address_province_code")
    private String defaultAddressProvinceCode;
    /**
     * 收货地址-名
     */
    @ApiModelProperty(value = "收货地址-名")
    @TableField(value = "shipping_address_first_name")
    private String shippingAddressFirstName;
    /**
     * 收货地址-地址1
     */
    @ApiModelProperty(value = "收货地址-地址1")
    @TableField(value = "shipping_address_address1")
    private String shippingAddressAddress1;
    /**
     * 收货地址-手机
     */
    @ApiModelProperty(value = "收货地址-手机")
    @TableField(value = "shipping_address_phone")
    private String shippingAddressPhone;
    /**
     * 收货地址-城市
     */
    @ApiModelProperty(value = "收货地址-城市")
    @TableField(value = "shipping_address_city")
    private String shippingAddressCity;
    /**
     * 收货地址-邮编
     */
    @ApiModelProperty(value = "收货地址-邮编")
    @TableField(value = "shipping_address_zip")
    private String shippingAddressZip;
    /**
     * 收货地址-省
     */
    @ApiModelProperty(value = "收货地址-省")
    @TableField(value = "shipping_address_province")
    private String shippingAddressProvince;
    /**
     * 收货地址--国家
     */
    @ApiModelProperty(value = "收货地址--国家")
    @TableField(value = "shipping_address_country")
    private String shippingAddressCountry;
    /**
     * 收货地址-姓
     */
    @ApiModelProperty(value = "收货地址-姓")
    @TableField(value = "shipping_address_last_name")
    private String shippingAddressLastName;
    /**
     * 收货地址-地址2
     */
    @ApiModelProperty(value = "收货地址-地址2")
    @TableField(value = "shipping_address_address2")
    private String shippingAddressAddress2;
    /**
     * 收货地址-公司名称
     */
    @ApiModelProperty(value = "收货地址-公司名称")
    @TableField(value = "shipping_address_company")
    private String shippingAddressCompany;
    /**
     * 收货地址-纬度
     */
    @ApiModelProperty(value = "收货地址-纬度")
    @TableField(value = "shipping_address_latitude")
    private Double shippingAddressLatitude;
    /**
     * 收货地址-经度
     */
    @ApiModelProperty(value = "收货地址-经度")
    @TableField(value = "shipping_address_longitude")
    private Double shippingAddressLongitude;
    /**
     * 收货地址-姓名
     */
    @ApiModelProperty(value = "收货地址-姓名")
    @TableField(value = "shipping_address_name")
    private String shippingAddressName;
    /**
     * 收货地址-国家编号
     */
    @ApiModelProperty(value = "收货地址-国家编号")
    @TableField(value = "shipping_address_country_code")
    private String shippingAddressCountryCode;
    /**
     * 收货地址-省编号
     */
    @ApiModelProperty(value = "收货地址-省编号")
    @TableField(value = "shipping_address_province_code")
    private String shippingAddressProvinceCode;
    /**
     * 信用卡编码
     */
    @ApiModelProperty(value = "信用卡编码")
    @TableField(value = "credit_card_bin")
    private String creditCardBin;
    /**
     * avs编码
     */
    @ApiModelProperty(value = "avs编码")
    @TableField(value = "avs_result_code")
    private String avsResultCode;
    /**
     * cvv编码
     */
    @ApiModelProperty(value = "cvv编码")
    @TableField(value = "cvv_result_code")
    private String cvvResultCode;
    /**
     * 银行
     */
    @ApiModelProperty(value = "银行")
    @TableField(value = "credit_card_company")
    private String creditCardCompany;
    /**
     * 银行卡号
     */
    @ApiModelProperty(value = "银行卡号")
    @TableField(value = "credit_card_number")
    private String creditCardNumber;
    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    @TableField(value = "customer_id")
    private String customerId;
    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    @TableField(value = "customer_code")
    private String customerCode;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    @TableField(value = "customer_name")
    private String customerName;
    /**
     * 网点编号
     */
    @ApiModelProperty(value = "网点编号")
    @TableField(value = "site_code")
    private String siteCode;
    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    @TableField(value = "shop_id")
    private Long shopId;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    @TableField(value = "shop_name")
    private String shopName;
    /**
     * 承运商id
     */
    @ApiModelProperty(value = "承运商id")
    @TableField(value = "carrier_id")
    private Integer carrierId;
    /**
     * 承运商名称
     */
    @ApiModelProperty(value = "承运商名称")
    @TableField(value = "carrier_name")
    private String carrierName;
    /**
     * 服务商编号
     */
    @ApiModelProperty(value = "服务商编号")
    @TableField(value = "trans_comp_code")
    private String transCompCode;
    /**
     * 服务商名称
     */
    @ApiModelProperty(value = "服务商名称")
    @TableField(value = "trans_comp_name")
    private String transCompName;
    /**
     * 产品代号
     */
    @ApiModelProperty(value = "产品代号")
    @TableField(value = "product_code")
    private String productCode;
    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    @TableField(value = "product_name")
    private String productName;
    /**
     * 预推荐仓库名称
     */
    @ApiModelProperty(value = "预推荐仓库名称")
    @TableField(value = "pre_warehouse_name")
    private String preWarehouseName;
    /**
     * 配送-id
     */
    @ApiModelProperty(value = "配送-id")
    @TableField(value = "shipping_id")
    private Double shippingId;
    /**
     * 配送-承运商
     */
    @ApiModelProperty(value = "配送-承运商")
    @TableField(value = "shipping_carrier_identifier")
    private String shippingCarrierIdentifier;
    /**
     * 配送-方式编码
     */
    @ApiModelProperty(value = "配送-方式编码")
    @TableField(value = "shipping_code")
    private String shippingCode;
    /**
     * 配送-交货类别
     */
    @ApiModelProperty(value = "配送-交货类别")
    @TableField(value = "shipping_delivery_category")
    private String shippingDeliveryCategory;
    /**
     * 配送-折扣价
     */
    @ApiModelProperty(value = "配送-折扣价")
    @TableField(value = "shipping_discounted_price")
    private String shippingDiscountedPrice;
    /**
     * 配送-电话
     */
    @ApiModelProperty(value = "配送-电话")
    @TableField(value = "shipping_phone")
    private String shippingPhone;
    /**
     * 配送-运费
     */
    @ApiModelProperty(value = "配送-运费")
    @TableField(value = "shipping_price")
    private String shippingPrice;
    /**
     * 配送-要求配送方式id
     */
    @ApiModelProperty(value = "配送-要求配送方式id")
    @TableField(value = "shipping_requested_fulfillment_service_id")
    private String shippingRequestedFulfillmentServiceId;
    /**
     * 配送-来源
     */
    @ApiModelProperty(value = "配送-来源")
    @TableField(value = "shipping_source")
    private String shippingSource;
    /**
     * 配送-方式
     */
    @ApiModelProperty(value = "配送-方式")
    @TableField(value = "shipping_title")
    private String shippingTitle;
    /**
     * 当前的订单状态
     */
    @ApiModelProperty(value = "当前的订单状态")
    @TableField(value = "order_status")
    private String orderStatus;

}