package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-04-23 15:18
 */
@Data
@ApiModel(value = "DelOutboundReassignExportListDto", description = "DelOutboundReassignExportListDto对象")
public class DelOutboundReassignExportListDto implements Serializable {

    @ApiModelProperty(value = "RefNo")
    private String refNo;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "处理点/仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "出库方式")
    private String orderType;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "物流服务")
    private String shipmentRule;

    @ApiModelProperty(value = "收件人名称")
    private String consignee;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "州/省")
    private String stateOrProvince;

    @ApiModelProperty(value = "城镇/城市")
    private String city;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "国家编码")
    private String countryCode;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "联系方式")
    private String phoneNo;

    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "COD")
    private BigDecimal codAmount;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "轨迹状态")
    private String trackingStatus;

    @ApiModelProperty(value = "最新轨迹")
    private String trackingDescription;

    @ApiModelProperty(value = "异常信息")
    private String exceptionMessage;

    @ApiModelProperty(value = "增值税号")
    private String ioss;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

}
