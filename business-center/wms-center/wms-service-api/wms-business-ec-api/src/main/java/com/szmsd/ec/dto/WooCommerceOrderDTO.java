package com.szmsd.ec.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * woocommerce订单信息主表
 * </p>
 *
 * @author lyf
 * @since 2022-05-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class WooCommerceOrderDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    @ApiModelProperty(value = "客户id")
    private String cusId;

    @ApiModelProperty("客户编码")
    private String cusCode;

    @ApiModelProperty("客户名称")
    private String cusName;

    @ApiModelProperty("店铺名称")
    private String shopName;

    @ApiModelProperty("店铺id")
    private Long shopId;

    @ApiModelProperty(value = "Woo Commerce 的唯一order ID，拿这个唯一号码作为平台订单号")
    private String wooCommerceOrderId;

    @ApiModelProperty(value = "父级id")
    private String parentId;

    @ApiModelProperty(value = "订单编号 和id一致")
    private String number;

    @ApiModelProperty(value = "Order key")
    private String orderKey;

    @ApiModelProperty(value = "订单来源")
    private String createdVia;

    @ApiModelProperty(value = "版本")
    private String version;

    @ApiModelProperty(value = "订单状态 pending, processing, on-hold, completed, cancelled, refunded, failed and trash")
    private String status;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "订单在站点的时区的创建日期")
    private Date dateCreated;

    @ApiModelProperty(value = "订单创建的GMT日期")
    private Date dateCreatedGmt;

    @ApiModelProperty(value = "订单在站点的时区的修改日期")
    private Date dateModified;

    @ApiModelProperty(value = "订单修改的GMT日期")
    private Date dateModifiedGmt;

    @ApiModelProperty(value = "订单的总折扣金额")
    private String discountTotal;

    @ApiModelProperty(value = "订单的总折扣税金额")
    private String discountTax;

    @ApiModelProperty(value = "订单的总装运金额")
    private String shippingTotal;

    @ApiModelProperty(value = "订单的总装运税金额")
    private String shippingTax;

    @ApiModelProperty(value = "当前项目税的总和")
    private String cartTax;

    @ApiModelProperty(value = "总计")
    private String total;

    @ApiModelProperty(value = "所有税费之和")
    private String totalTax;

    @ApiModelProperty(value = "结账时价格是否含税")
    private Integer pricesIncludeTax;

    @ApiModelProperty(value = "负责订单的用户ID。0给散客。默认为0")
    private String customerId;

    @ApiModelProperty(value = "客户的IP地址")
    private String customerIpAddress;

    @ApiModelProperty(value = "客户的用户代理")
    private String customerUserAgent;

    @ApiModelProperty(value = "客户备注")
    private String customerNote;

    @ApiModelProperty(value = "账单人姓名")
    private String billFirstName;

    @ApiModelProperty(value = "账单人姓名2")
    private String billLastName;

    @ApiModelProperty(value = "账单人公司名称")
    private String billCompany;

    @ApiModelProperty(value = "账单人地址1")
    private String billAddress1;

    @ApiModelProperty(value = "账单人地址1")
    private String billAddress2;

    @ApiModelProperty(value = "账单人城市")
    private String billCity;

    @ApiModelProperty(value = "账单人区")
    private String billState;

    @ApiModelProperty(value = "账单人邮政编码")
    private String billPostcode;

    @ApiModelProperty(value = "账单人国家")
    private String billCountry;

    @ApiModelProperty(value = "账单人邮箱")
    private String billEmail;

    @ApiModelProperty(value = "账单人电话")
    private String billPhone;

    @ApiModelProperty(value = "收货人姓名")
    private String shipFirstName;

    @ApiModelProperty(value = "收货人姓名2")
    private String shipLastName;

    @ApiModelProperty(value = "收货人公司名称")
    private String shipCompany;

    @ApiModelProperty(value = "收货人地址1")
    private String shipAddress1;

    @ApiModelProperty(value = "收货人地址1")
    private String shipAddress2;

    @ApiModelProperty(value = "收货人城市")
    private String shipCity;

    @ApiModelProperty(value = "收货人区")
    private String shipState;

    @ApiModelProperty(value = "收货人邮政编码")
    private String shipPostcode;

    @ApiModelProperty(value = "收货人国家")
    private String shipCountry;

    @ApiModelProperty(value = "支付方式ID")
    private String paymentMethod;

    @ApiModelProperty(value = "支付方式名称")
    private String paymentMethodTitle;

    @ApiModelProperty(value = "唯一事务ID")
    private String transactionId;

    @ApiModelProperty(value = "在该站点的时区支付订单的日期")
    private Date datePaid;

    @ApiModelProperty(value = "订单支付GMT日期")
    private Date datePaidGmt;

    @ApiModelProperty(value = "在该站点的时区订单完成日期")
    private Date dateCompleted;

    @ApiModelProperty(value = "订单的完成GMT日期")
    private Date dateCompletedGmt;

    @ApiModelProperty(value = "购物车MD5")
    private String cartHash;

    @ApiModelProperty(value = "元数据JSON")
    private String metaData;

    @ApiModelProperty(value = "税率信息JSON")
    private String taxLines;

    @ApiModelProperty(value = "收货信息JSON")
    private String shippingLines;

    @ApiModelProperty(value = "费用信息JSON")
    private String feeLines;

    @ApiModelProperty(value = "优惠信息JSON")
    private String couponLines;

    @ApiModelProperty(value = "退款信息JSON")
    private String refundLines;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty("商品信息")
    private List<WooCommerceOrderItemDTO> orderItems;


}
