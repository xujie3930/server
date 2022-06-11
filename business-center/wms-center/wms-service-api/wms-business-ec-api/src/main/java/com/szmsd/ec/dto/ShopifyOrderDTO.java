package com.szmsd.ec.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author hyx
 * @version V1.0
 * @ClassName:ShopifyOrder
 * @Description: oms_shopify_order shopify订单表DTO
 * @date 2021-04-15 15:14:09
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class ShopifyOrderDTO {
    private static final long serialVersionUID = -38881021984978270L;

    private Long id;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

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

    @ApiModelProperty(value = "${column.comment}")
    private String appId;
    /**
     * ip地址
     */
    @ApiModelProperty(value = "ip地址")
    private String browserIp;
    /**
     * 买家是否接受营销
     */
    @ApiModelProperty(value = "买家是否接受营销")
    private String buyerAcceptsMarketing;
    /**
     * 取消原因  customer客户：客户取消了订单, fraud欺诈：订单是欺诈性的,inventory库存：订单中的商品库存不足,declined拒绝：付款被拒绝,other其他
     */
    @ApiModelProperty(value = "取消原因  customer客户：客户取消了订单, fraud欺诈：订单是欺诈性的,inventory库存：订单中的商品库存不足,declined拒绝：付款被拒绝,other其他")
    private String cancelReason;
    /**
     * 取消订单的日期和时间
     */
    @ApiModelProperty(value = "取消订单的日期和时间")
    private Date cancelledAt;
    /**
     * 与订单关联的购物车的ID
     */
    @ApiModelProperty(value = "与订单关联的购物车的ID")
    private String cartToken;
    /**
     * 检验id
     */
    @ApiModelProperty(value = "检验id")
    private String checkoutId;
    /**
     * 检验token
     */
    @ApiModelProperty(value = "检验token")
    private String checkoutToken;
    /**
     * 关闭时间
     */
    @ApiModelProperty(value = "关闭时间")
    private Date closedAt;
    /**
     * 是否确认
     */
    @ApiModelProperty(value = "是否确认")
    private String confirmed;
    /**
     * email联系
     */
    @ApiModelProperty(value = "email联系")
    private String contactEmail;
    /**
     * 订单创建时间
     */
    @ApiModelProperty(value = "订单创建时间")
    private Date createdAt;
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String currency;
    /**
     * 小计金额
     */
    @ApiModelProperty(value = "小计金额")
    private Double currentSubtotalPrice;
    /**
     * 折扣价
     */
    @ApiModelProperty(value = "折扣价")
    private Double currentTotalDiscounts;
    /**
     * 关税金额
     */
    @ApiModelProperty(value = "关税金额")
    private Double currentTotalDutiesSet;
    /**
     * 总金额
     */
    @ApiModelProperty(value = "总金额")
    private Double currentTotalPrice;
    /**
     * 税额
     */
    @ApiModelProperty(value = "税额")
    private Double currentTotalTax;
    /**
     * 客户地区
     */
    @ApiModelProperty(value = "客户地区")
    private String customerLocale;
    /**
     * 客户设备id
     */
    @ApiModelProperty(value = "客户设备id")
    private String deviceId;
    /**
     * email
     */
    @ApiModelProperty(value = "email")
    private String email;
    /**
     * 支付状态：pending待处理,authorized已授权,partially_paid 部分支付,paid已支付,partially_refunded已部分退款,refunded已退款,voided作废
     */
    @ApiModelProperty(value = "支付状态：pending待处理,authorized已授权,partially_paid 部分支付,paid已支付,partially_refunded已部分退款,refunded已退款,voided作废")
    private String financialStatus;
    /**
     * 发货状态：fulfilled已完成,null未发货,partial部分发货,restocked缺货
     */
    @ApiModelProperty(value = "发货状态：fulfilled已完成,null未发货,partial部分发货,restocked缺货")
    private String fulfillmentStatus;
    /**
     * 支付通道：人工
     */
    @ApiModelProperty(value = "支付通道：人工")
    private String gateway;
    /**
     * 登录网址
     */
    @ApiModelProperty(value = "登录网址")
    private String landingSite;
    /**
     * 登录网址链接
     */
    @ApiModelProperty(value = "登录网址链接")
    private String landingSiteRef;
    /**
     * 地点id
     */
    @ApiModelProperty(value = "地点id")
    private String locationId;
    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String note;
    /**
     * 商品数量
     */
    @ApiModelProperty(value = "商品数量")
    private Double number;
    /**
     * 订单号
     */
    @ApiModelProperty(value = "订单号")
    private Integer orderNumber;
    /**
     * 订单链接
     */
    @ApiModelProperty(value = "订单链接")
    private String orderStatusUrl;
    /**
     * 原始关税
     */
    @ApiModelProperty(value = "原始关税")
    private String originalTotalDutiesSet;
    /**
     * 联系电话
     */
    @ApiModelProperty(value = "联系电话")
    private String phone;
    /**
     * 币种
     */
    @ApiModelProperty(value = "币种")
    private String presentmentCurrency;
    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间")
    private Date processedAt;
    /**
     * 操作方式
     */
    @ApiModelProperty(value = "操作方式")
    private String processingMethod;
    /**
     * 引用
     */
    @ApiModelProperty(value = "引用")
    private String reference;
    /**
     * 参考网站
     */
    @ApiModelProperty(value = "参考网站")
    private String referringSite;
    /**
     * 源标识符
     */
    @ApiModelProperty(value = "源标识符")
    private String sourceIdentifier;
    /**
     * 来源名字：shopify草稿订单
     */
    @ApiModelProperty(value = "来源名字：shopify草稿订单")
    private String sourceName;
    /**
     * 来源地址
     */
    @ApiModelProperty(value = "来源地址")
    private String sourceUrl;
    /**
     * 小计  = 总价 - 折扣
     */
    @ApiModelProperty(value = "小计  = 总价 - 折扣")
    private Double subtotalPrice;
    /**
     * 标签
     */
    @ApiModelProperty(value = "标签")
    private String tags;
    /**
     * 是否含税
     */
    @ApiModelProperty(value = "是否含税")
    private String taxesIncluded;
    /**
     * 是否是测试单
     */
    @ApiModelProperty(value = "是否是测试单")
    private String test;
    /**
     * 请求token
     */
    @ApiModelProperty(value = "请求token")
    private String token;
    /**
     * 折扣金额
     */
    @ApiModelProperty(value = "折扣金额")
    private Double totalDiscounts;
    /**
     * 商品总价
     */
    @ApiModelProperty(value = "商品总价")
    private Double totalLineItemsPrice;
    /**
     * 未清金额
     */
    @ApiModelProperty(value = "未清金额")
    private Double totalOutstanding;
    /**
     * 总计
     */
    @ApiModelProperty(value = "总计")
    private Double totalPrice;
    /**
     * 总美金
     */
    @ApiModelProperty(value = "总美金")
    private Double totalPriceUsd;
    /**
     * 税费
     */
    @ApiModelProperty(value = "税费")
    private Double totalTax;
    /**
     * 总收款
     */
    @ApiModelProperty(value = "总收款")
    private Double totalTipReceived;
    /**
     * 总重
     */
    @ApiModelProperty(value = "总重")
    private Double totalWeight;
    /**
     * 更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date updatedAt;
    /**
     * shopify用户id
     */
    @ApiModelProperty(value = "shopify用户id")
    private Integer userId;
    /**
     * 账单用户-名
     */
    @ApiModelProperty(value = "账单用户-名")
    private String billingAddressFirstName;
    /**
     * 账单地址
     */
    @ApiModelProperty(value = "账单地址")
    private String billingAddressAddress1;
    /**
     * 账单地址-手机
     */
    @ApiModelProperty(value = "账单地址-手机")
    private String billingAddressPhone;
    /**
     * 账单地址-城市
     */
    @ApiModelProperty(value = "账单地址-城市")
    private String billingAddressCity;
    /**
     * 账单地址-邮编
     */
    @ApiModelProperty(value = "账单地址-邮编")
    private String billingAddressZip;
    /**
     * 账单地址-省
     */
    @ApiModelProperty(value = "账单地址-省")
    private String billingAddressProvince;
    /**
     * 账单地址-国家
     */
    @ApiModelProperty(value = "账单地址-国家")
    private String billingAddressCountry;
    /**
     * 账单用户-姓
     */
    @ApiModelProperty(value = "账单用户-姓")
    private String billingAddressLastName;
    /**
     * 账单地址-地址2
     */
    @ApiModelProperty(value = "账单地址-地址2")
    private String billingAddressAddress2;
    /**
     * 账单地址-公司名称
     */
    @ApiModelProperty(value = "账单地址-公司名称")
    private String billingAddressCompany;
    /**
     * 账单地址-纬度
     */
    @ApiModelProperty(value = "账单地址-纬度")
    private Double billingAddressLatitude;
    /**
     * 账单地址-经度
     */
    @ApiModelProperty(value = "账单地址-经度")
    private Double billingAddressLongitude;
    /**
     * 账单名称
     */
    @ApiModelProperty(value = "账单名称")
    private String billingAddressName;
    /**
     * 账单地址-国家编号
     */
    @ApiModelProperty(value = "账单地址-国家编号")
    private String billingAddressCountryCode;
    /**
     * 账单地址-省编号
     */
    @ApiModelProperty(value = "账单地址-省编号")
    private String billingAddressProvinceCode;
    /**
     * 客户-名
     */
    @ApiModelProperty(value = "客户-名")
    private String customerFirstName;
    /**
     * 客户-地址1
     */
    @ApiModelProperty(value = "客户-地址1")
    private String customerAddress1;
    /**
     * 客户-手机
     */
    @ApiModelProperty(value = "客户-手机")
    private String customerPhone;
    /**
     * 客户-城市
     */
    @ApiModelProperty(value = "客户-城市")
    private String customerCity;
    /**
     * 客户-邮编
     */
    @ApiModelProperty(value = "客户-邮编")
    private String customerZip;
    /**
     * 客户-省
     */
    @ApiModelProperty(value = "客户-省")
    private String customerProvince;
    /**
     * 客户-国家
     */
    @ApiModelProperty(value = "客户-国家")
    private String customerCountry;
    /**
     * 客户-姓
     */
    @ApiModelProperty(value = "客户-姓")
    private String customerLastName;
    /**
     * 客户-地址2
     */
    @ApiModelProperty(value = "客户-地址2")
    private String customerAddress2;
    /**
     * 客户-公司名称
     */
    @ApiModelProperty(value = "客户-公司名称")
    private String customerCompany;
    /**
     * 客户-纬度
     */
    @ApiModelProperty(value = "客户-纬度")
    private Double customerLatitude;
    /**
     * 客户-经度
     */
    @ApiModelProperty(value = "客户-经度")
    private Double customerLongitude;
    /**
     * 客户-姓名
     */
    @ApiModelProperty(value = "客户-姓名")
    private String customerShopifyName;
    /**
     * 客户-国家编号
     */
    @ApiModelProperty(value = "客户-国家编号")
    private String customerCountryCode;
    /**
     * 客户-省编号
     */
    @ApiModelProperty(value = "客户-省编号")
    private String customerProvinceCode;
    /**
     * 默认收件人-名
     */
    @ApiModelProperty(value = "默认收件人-名")
    private String defaultAddressFirstName;
    /**
     * 默认收件人-地址1
     */
    @ApiModelProperty(value = "默认收件人-地址1")
    private String defaultAddressAddress1;
    /**
     * 默认收件人-手机
     */
    @ApiModelProperty(value = "默认收件人-手机")
    private String defaultAddressPhone;
    /**
     * 默认收件人-城市
     */
    @ApiModelProperty(value = "默认收件人-城市")
    private String defaultAddressCity;
    /**
     * 默认收件人-邮编
     */
    @ApiModelProperty(value = "默认收件人-邮编")
    private String defaultAddressZip;
    /**
     * 默认收件人-省
     */
    @ApiModelProperty(value = "默认收件人-省")
    private String defaultAddressProvince;
    /**
     * 默认收件人-国家
     */
    @ApiModelProperty(value = "默认收件人-国家")
    private String defaultAddressCountry;
    /**
     * 默认收件人-姓
     */
    @ApiModelProperty(value = "默认收件人-姓")
    private String defaultAddressLastName;
    /**
     * 默认收件人-地址2
     */
    @ApiModelProperty(value = "默认收件人-地址2")
    private String defaultAddressAddress2;
    /**
     * 默认收件人-公司名称
     */
    @ApiModelProperty(value = "默认收件人-公司名称")
    private String defaultAddressCompany;
    /**
     * 默认收件人-纬度
     */
    @ApiModelProperty(value = "默认收件人-纬度")
    private Double defaultAddressLatitude;
    /**
     * 默认收件人-经度
     */
    @ApiModelProperty(value = "默认收件人-经度")
    private Double defaultAddressLongitude;
    /**
     * 默认收件人-姓名
     */
    @ApiModelProperty(value = "默认收件人-姓名")
    private String defaultAddressName;
    /**
     * 默认收件人-国家编号
     */
    @ApiModelProperty(value = "默认收件人-国家编号")
    private String defaultAddressCountryCode;
    /**
     * 默认收件人-省编号
     */
    @ApiModelProperty(value = "默认收件人-省编号")
    private String defaultAddressProvinceCode;
    /**
     * 收货地址-名
     */
    @ApiModelProperty(value = "收货地址-名")
    private String shippingAddressFirstName;
    /**
     * 收货地址-地址1
     */
    @ApiModelProperty(value = "收货地址-地址1")
    private String shippingAddressAddress1;
    /**
     * 收货地址-手机
     */
    @ApiModelProperty(value = "收货地址-手机")
    private String shippingAddressPhone;
    /**
     * 收货地址-城市
     */
    @ApiModelProperty(value = "收货地址-城市")
    private String shippingAddressCity;
    /**
     * 收货地址-邮编
     */
    @ApiModelProperty(value = "收货地址-邮编")
    private String shippingAddressZip;
    /**
     * 收货地址-省
     */
    @ApiModelProperty(value = "收货地址-省")
    private String shippingAddressProvince;
    /**
     * 收货地址--国家
     */
    @ApiModelProperty(value = "收货地址--国家")
    private String shippingAddressCountry;
    /**
     * 收货地址-姓
     */
    @ApiModelProperty(value = "收货地址-姓")
    private String shippingAddressLastName;
    /**
     * 收货地址-地址2
     */
    @ApiModelProperty(value = "收货地址-地址2")
    private String shippingAddressAddress2;
    /**
     * 收货地址-公司名称
     */
    @ApiModelProperty(value = "收货地址-公司名称")
    private String shippingAddressCompany;
    /**
     * 收货地址-纬度
     */
    @ApiModelProperty(value = "收货地址-纬度")
    private Double shippingAddressLatitude;
    /**
     * 收货地址-经度
     */
    @ApiModelProperty(value = "收货地址-经度")
    private Double shippingAddressLongitude;
    /**
     * 收货地址-姓名
     */
    @ApiModelProperty(value = "收货地址-姓名")
    private String shippingAddressName;
    /**
     * 收货地址-国家编号
     */
    @ApiModelProperty(value = "收货地址-国家编号")
    private String shippingAddressCountryCode;
    /**
     * 收货地址-省编号
     */
    @ApiModelProperty(value = "收货地址-省编号")
    private String shippingAddressProvinceCode;
    /**
     * 信用卡编码
     */
    @ApiModelProperty(value = "信用卡编码")
    private String creditCardBin;
    /**
     * avs编码
     */
    @ApiModelProperty(value = "avs编码")
    private String avsResultCode;
    /**
     * cvv编码
     */
    @ApiModelProperty(value = "cvv编码")
    private String cvvResultCode;
    /**
     * 银行
     */
    @ApiModelProperty(value = "银行")
    private String creditCardCompany;
    /**
     * 银行卡号
     */
    @ApiModelProperty(value = "银行卡号")
    private String creditCardNumber;
    /**
     * 客户id
     */
    @ApiModelProperty(value = "客户id")
    private String customerId;
    /**
     * 客户编号
     */
    @ApiModelProperty(value = "客户编号")
    private String customerCode;
    /**
     * 客户名称
     */
    @ApiModelProperty(value = "客户名称")
    private String customerName;
    /**
     * 网点编号
     */
    @ApiModelProperty(value = "网点编号")
    private String siteCode;
    /**
     * 店铺id
     */
    @ApiModelProperty(value = "店铺id")
    private Long shopId;
    /**
     * 店铺名称
     */
    @ApiModelProperty(value = "店铺名称")
    private String shopName;
    /**
     * 承运商id
     */
    @ApiModelProperty(value = "承运商id")
    private Integer carrierId;
    /**
     * 承运商名称
     */
    @ApiModelProperty(value = "承运商名称")
    private String carrierName;
    /**
     * 服务商编号
     */
    @ApiModelProperty(value = "服务商编号")
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
     * 预推荐仓库名称
     */
    @ApiModelProperty(value = "预推荐仓库名称")
    private String preWarehouseName;
    /**
     * 配送-id
     */
    @ApiModelProperty(value = "配送-id")
    private Double shippingId;
    /**
     * 配送-承运商
     */
    @ApiModelProperty(value = "配送-承运商")
    private String shippingCarrierIdentifier;
    /**
     * 配送-方式编码
     */
    @ApiModelProperty(value = "配送-方式编码")
    private String shippingCode;
    /**
     * 配送-交货类别
     */
    @ApiModelProperty(value = "配送-交货类别")
    private String shippingDeliveryCategory;
    /**
     * 配送-折扣价
     */
    @ApiModelProperty(value = "配送-折扣价")
    private String shippingDiscountedPrice;
    /**
     * 配送-电话
     */
    @ApiModelProperty(value = "配送-电话")
    private String shippingPhone;
    /**
     * 配送-运费
     */
    @ApiModelProperty(value = "配送-运费")
    private String shippingPrice;
    /**
     * 配送-要求配送方式id
     */
    @ApiModelProperty(value = "配送-要求配送方式id")
    private String shippingRequestedFulfillmentServiceId;
    /**
     * 配送-来源
     */
    @ApiModelProperty(value = "配送-来源")
    private String shippingSource;
    /**
     * 配送-方式
     */
    @ApiModelProperty(value = "配送-方式")
    private String shippingTitle;
    /**
     * 当前的订单状态
     */
    @ApiModelProperty(value = "当前的订单状态")
    private String orderStatus;
    @ApiModelProperty(value = "客户简称")
    private String customerShortName;

    private List<ShopifyOrderItemDTO> shopifyOrderItemDTOList;
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

    @ApiModelProperty(value = "发货仓库id")
    private Long shippingWarehouseId;

    @ApiModelProperty(value = "发货仓库名称")
    private String shippingWarehouseName;

    /**
     * 客户提交销售订单的日期(查询条件集合)
     */
    @ApiModelProperty(value = "客户提交销售订单的日期(查询条件集合)")
    private String[] createDates;

}