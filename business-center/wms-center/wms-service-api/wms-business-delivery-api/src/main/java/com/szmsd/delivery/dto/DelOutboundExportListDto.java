package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
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
@ApiModel(value = "DelOutboundExportListDto", description = "DelOutboundExportListDto对象")
public class DelOutboundExportListDto implements Serializable {

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "处理点/仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "物流单号")
    private String shipmentOrderNumber;

    @ApiModelProperty(value = "RefNo")
    private String refOrderNo;

    @ApiModelProperty(value = "供应商服务代码")
    private String shipmentService;

    @ExcelProperty(value = "提审时间")
    private Date bringVerifyTime;

    @ExcelProperty(value = "到仓时间")
    private Date arrivalTime;

    @ExcelProperty(value = "发货时间")
    private Date shipmentsTime;

    @ApiModelProperty(value = "异常状态")
    private String exceptionState;

    @ApiModelProperty(value = "服务产品代码")
    private String shipmentRule;

    @ApiModelProperty(value = "箱数")
    private Long boxNumber;

    @ApiModelProperty(value = "重量")
    private Double weight;

    @ApiModelProperty(value = "计费重")
    private BigDecimal calcWeight;

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

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "联系方式")
    private String phoneNo;

    @ApiModelProperty(value = "规格")
    private String specifications;
}
