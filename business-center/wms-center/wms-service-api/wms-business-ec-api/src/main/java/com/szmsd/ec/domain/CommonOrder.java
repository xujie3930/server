package com.szmsd.ec.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * <p>
 * 电商平台公共订单表
 * </p>
 *
 * @author zengfanlang
 * @since 2021-12-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "电商平台公共订单表", description = "EcCommonOrder对象")
@TableName("ec_common_order")
public class CommonOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "")
    private Long id;

    @ApiModelProperty(value = "客户id")
    private String cusId;

    @ApiModelProperty(value = "客户编号")
    @Excel(name = "客户编号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "店铺id")
    @Excel(name = "店铺id")
    private Long shopId;

    @ApiModelProperty(value = "店铺名称")
    @Excel(name = "店铺名称")
    private String shopName;

    @ApiModelProperty(value = "订单编号")
    @Excel(name = "订单编号")
    private String orderNo;

    @ApiModelProperty(value = "平台订单编号")
    @Excel(name = "平台订单编号")
    private String platformOrderNumber;

    @ApiModelProperty(value = "订单日期")
    @Excel(name = "订单日期")
    private Date orderDate;

    @ApiModelProperty(value = "电商平台")
    @Excel(name = "电商平台")
    private String orderSource;

    @ApiModelProperty(value = "订单类型")
    @Excel(name = "订单类型")
    private String orderType;

    @ApiModelProperty(value = "订单状态")
    @Excel(name = "订单状态")
    private String status;

    @ApiModelProperty(value = "销售渠道")
    @Excel(name = "销售渠道")
    private String salesChannels;

    @ApiModelProperty(value = "仓库编号")
    @Excel(name = "仓库编号")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    @Excel(name = "仓库名称")
    private String warehouseName;

    @ApiModelProperty(value = "收货人")
    @Excel(name = "收货人")
    private String receiver;

    @ApiModelProperty(value = "收货人电话")
    @Excel(name = "收货人电话")
    private String receiverPhone;

    @ApiModelProperty(value = "收货国家")
    @Excel(name = "收货国家")
    private String receiverCountryName;

    @ApiModelProperty(value = "收货国家code")
    @Excel(name = "收货国家code")
    private String receiverCountryCode;

    @ApiModelProperty(value = "收货省份")
    @Excel(name = "收货省份")
    private String receiverProvinceName;

    @ApiModelProperty(value = "收货省份Code")
    @Excel(name = "收货省份Code")
    private String receiverProvinceCode;

    @ApiModelProperty(value = "收货城市")
    @Excel(name = "收货城市")
    private String receiverCityName;

    @ApiModelProperty(value = "收货地址1")
    @Excel(name = "收货地址1")
    private String receiverAddress1;

    @ApiModelProperty(value = "收货地址2")
    @Excel(name = "收货地址2")
    private String receiverAddress2;

    @ApiModelProperty(value = "收货邮编")
    @Excel(name = "收货邮编")
    private String receiverPostcode;

    @ApiModelProperty(value = "派送日期开始")
    @Excel(name = "派送日期开始")
    private Date deliveryDateStarts;

    @ApiModelProperty(value = "派送日期结束")
    @Excel(name = "派送日期结束")
    private Date deliveryDateEnd;

    @ApiModelProperty(value = "配送渠道")
    @Excel(name = "配送渠道")
    private String shippingChannel;

    @ApiModelProperty(value = "运单号")
    @Excel(name = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "金额")
    @Excel(name = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币别")
    @Excel(name = "币别")
    private String currency;

    @ApiModelProperty(value = "推送订单中心的方法")
    private String pushMethod;

    @ApiModelProperty(value = "推送订单中心的返回消息体")
    private String pushResultMsg;

    @ApiModelProperty(value = "转仓库单异常信息")
    private String transferErrorMsg;

    @ApiModelProperty(value = "创建履约单状态 1成功0失败")
    private String fulfillmentStatus;

    @ApiModelProperty(value = "履约单id")
    private String fulfillmentId;

    @ApiModelProperty(value = "履约单 locationId")
    private String locationId;

    @ApiModelProperty("平台状态")
    @Excel(name = "平台状态")
    private String platformOrderStatus;

    @ApiModelProperty("订单来源")
    @Excel(name = "订单来源")
    private String orderVia;

    @ApiModelProperty(value = "物流单号")
    @Excel(name = "物流单号")
    private String transferNumber;

    @ApiModelProperty(value = "发货方式")
    @Excel(name = "发货方式")
    private String shippingMethod;

    @ApiModelProperty(value = "发货服务")
    @Excel(name = "发货服务")
    private String shippingService;

    @ApiModelProperty(value = "发货仓库id")
    private Long shippingWarehouseId;

    @ApiModelProperty(value = "发货仓库名称")
    @Excel(name = "发货仓库名称")
    private String shippingWarehouseName;

    @ApiModelProperty(value = "item")
    @TableField(exist = false)
    private List<CommonOrderItem> commonOrderItemList;

}
