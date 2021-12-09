package com.szmsd.exception.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExceptionInfoExportDto {

    @ApiModelProperty(value = "异常号")
    private String exceptionNo;

    @ExcelIgnore
    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "状态名称")
    private String stateName;

    @ApiModelProperty(value = "异常类型名称")
    private String exceptionTypeName;

    @ApiModelProperty(value = "订单类型名称")
    private String orderTypeName;

    @ApiModelProperty(value = "仓库备注")
    private String remark;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "异常描述")
    private String exceptionMessage;

    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "收件人")
    private String consignee;

    @ApiModelProperty(value = "街道1")
    private String street1;

    @ApiModelProperty(value = "街道2")
    private String street2;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "省份/州")
    private String stateOrProvince;

    @ApiModelProperty(value = "国家名称")
    private String country;

    @ApiModelProperty(value = "邮编")
    private String postCode;

}
