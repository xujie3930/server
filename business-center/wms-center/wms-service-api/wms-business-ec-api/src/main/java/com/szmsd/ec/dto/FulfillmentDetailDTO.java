package com.szmsd.ec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @FileName FulfillmentDetailDto.java
 * @Description ----------功能描述---------
 * @Date 2021-04-16 18:19
 * @Author hyx
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class FulfillmentDetailDTO {
    /**
     * 订单明细id
     */
    @ApiModelProperty(value = "订单明细id")
    private String orderItemId;

    /**
     * 卖家订单id
     */
    @ApiModelProperty(value = "卖家订单id")
    private String sellerOrderId;

    /**
     * 状态订单的个数
     */
    @ApiModelProperty(value = "状态订单的个数")
    private Integer stateQuantityAmount;
    /**
     * 状态订单的个数单位
     */
    @ApiModelProperty(value = "状态订单的个数单位")
    private String stateQuantityUnit;

    /**
     * 服务商名称
     */
    @ApiModelProperty(value = "服务商名称")
    private String transCompName;

    /**
     * 产品名称
     */
    @ApiModelProperty(value = "产品名称")
    private String productName;
    /**
     * 快递单编号
     */
    @ApiModelProperty(value = "快递单编号")
    private String expressCode;

    /**
     * 物流轨迹
     */
    @ApiModelProperty(value = "物流轨迹")
    private String trackingURL;

    private String returnCenterAddressName;
    /**
     * 退货地址1
     */
    @ApiModelProperty(value = "退货地址1")
    private String returnCenterAddressAddress1;
    /**
     * 退货地址2
     */
    @ApiModelProperty(value = "退货地址2")
    private String returnCenterAddressAddress2;
    /**
     * 退货城市
     */
    @ApiModelProperty(value = "退货城市")
    private String returnCenterAddressCity;
    /**
     * 退货省市区
     */
    @ApiModelProperty(value = "退货省市区")
    private String returnCenterAddressState;
    /**
     * 退货省市区
     */
    @ApiModelProperty(value = "退货省市区")
    private String returnCenterAddressPostalCode;
    /**
     * 退货国家
     */
    @ApiModelProperty(value = "退货国家")
    private String returnCenterAddressCountry;
    /**
     * 退货电话
     */
    @ApiModelProperty(value = "退货电话")
    private String returnCenterAddressDayPhone;
    /**
     * 退货电子邮件
     */
    @ApiModelProperty(value = "退货电子邮件")
    private String returnCenterAddressEmailId;

    /**
     * 发货时间
     */
    @ApiModelProperty(value = "发货时间")
    private String shipDateTime;
}