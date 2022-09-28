package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "国内直发excel数据返回")
public class BillDirectDeliveryTotalVO {

    @ApiModelProperty(value = "性质")
    private String businessCategory;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "跟踪ID")
    private String traceId;

    @ApiModelProperty(value = "计量重(单位)")
    private String calcWeightUnit;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @ApiModelProperty(value = "refNo")
    private String refNo;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "业务类型")
    private String orderType;

    @ApiModelProperty(value = "发货仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "目的地")
    private String destinationDelivery;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "sku")
    private String sku;







}
