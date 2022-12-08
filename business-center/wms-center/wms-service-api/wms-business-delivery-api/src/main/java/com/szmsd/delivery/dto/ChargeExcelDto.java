package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ChargeExcelDto implements Serializable {

    @ApiModelProperty(value = "DM单号")
    private String orderNo;

    @ApiModelProperty(value = "实重")
    private BigDecimal weight;

    @ApiModelProperty(value = "计费重")
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "重量单位")
    private String weightUnit;

    @ApiModelProperty(value = "规格")
    private String specifications;

    @ApiModelProperty(value = "报价表编号")
    private String quotationNo;
}
