package com.szmsd.ec.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.ec.domain.CommonOrderItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推送到wms 出库订单DTO
 */
@Data
public class PushWmsOrderDTO {

    @ApiModelProperty(value = "订单号")
    private String cusOrderNo;

    @ApiModelProperty(value = "平台订单编号")
    private String platformOrderNumber;

    @ApiModelProperty(value = "客户id")
    private String cusId;

    @ApiModelProperty(value = "客户编号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    private String shopId;

    private String shopName;

    @ApiModelProperty(value = "订单来源")
    private String orderFrom;

    @ApiModelProperty(value = "收件人")
    private String receiver;

    @ApiModelProperty(value = "收件电话")
    private String receiverPhone;

    @ApiModelProperty(value = "收件人邮箱")
    private String receiverEmail;

    @ApiModelProperty(value = "收件人邮编")
    private String receiverPostcode;

    @ApiModelProperty(value = "收件人国家")
    private String receiverCountryName;

    @ApiModelProperty(value = "收件人国家代码")
    private String receiverCountryCode;

    @ApiModelProperty(value = "收件人省份")
    private String receiverProvinceName;

    @ApiModelProperty(value = "收件人州省代码")
    private String receiverProvinceCode;

    @ApiModelProperty(value = "收件人城市NAME")
    private String receiverCityName;

    @ApiModelProperty(value = "城市代码CODE")
    private String receiverCityCode;

    @ApiModelProperty(value = "收件人区县NAME")
    private String receiverAreaName;

    @ApiModelProperty(value = "收件人区县CODE")
    private String receiverAreaCode;

    @ApiModelProperty(value = "收件人城镇NAME")
    private String receiverTownName;

    @ApiModelProperty(value = "收件人城镇CODE")
    private String receiverTownCode;

    @ApiModelProperty(value = "收件人地址")
    private String receiverAddress;

    @ApiModelProperty(value = "收件备注")
    private String receiverRemark;

//    @ApiModelProperty(value = "发件人")
//    private String sender;
//
//    @ApiModelProperty(value = "发件电话")
//    private String senderPhone;
//
//    @ApiModelProperty(value = "发件人邮箱")
//    private String senderEmail;
//
//    @ApiModelProperty(value = "发件人邮编")
//    private String senderPostcode;
//
//    @ApiModelProperty(value = "发件人国家")
//    private String senderCountryName;
//
//    @ApiModelProperty(value = "发件人国家代码")
//    private String senderCountryCode;
//
//    @ApiModelProperty(value = "发件人省份")
//    private String senderProvinceName;
//
//    @ApiModelProperty(value = "发件人州省代码")
//    private String senderProvinceCode;
//
//    @ApiModelProperty(value = "发件人城市")
//    private String senderCityName;
//
//    @ApiModelProperty(value = "发件人城市代码")
//    private String senderCityCode;
//
//    @ApiModelProperty(value = "发件人区县NAME")
//    private String senderAreaName;
//
//    @ApiModelProperty(value = "发件人区县CODE")
//    private String senderAreaCode;
//
//    @ApiModelProperty(value = "发件人城镇NAME")
//    private String senderTownName;
//
//    @ApiModelProperty(value = "发件人城镇CODE")
//    private String senderTownCode;
//
//    @ApiModelProperty(value = "发件人地址")
//    private String senderAddress;
//
    @ApiModelProperty(value = "价值金额")
    private BigDecimal valueAmount;

    @ApiModelProperty(value = "价值金额币种编码-数据字典")
    private String valueAmountCurrencyCode;

    @ApiModelProperty(value = "价值金额币种名称-数据字典")
    private String valueAmountCurrencyName;

    @ApiModelProperty(value = "item")
    private List<CommonOrderItem> commonOrderItemList;
}
