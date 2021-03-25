package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedCharge", description = "基础费用（运费）")
public class PricedCharge {

    @ApiModelProperty("分区")
    private String zone;

    @ApiModelProperty("最小重量")
    private BigDecimal minWeight;

    @ApiModelProperty("最大重量")
    private BigDecimal maxWeight;

    @ApiModelProperty
    private ChargeFormula formula;

}
