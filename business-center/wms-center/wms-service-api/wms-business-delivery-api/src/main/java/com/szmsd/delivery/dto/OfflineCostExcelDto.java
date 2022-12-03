package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class OfflineCostExcelDto implements Serializable {

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

    @ApiModelProperty(value = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "费用类型")
    private String chargeType;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币别")
    private String currencyCode;
}
