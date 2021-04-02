package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedChargeVO", description = "价格表")
public class PricedChargeVO {

    @ApiModelProperty("分区名称 - 分区")
    private String zone;

    @ApiModelProperty("最小重量")
    private BigDecimal minWeight;

    @ApiModelProperty("最大重量")
    private BigDecimal maxWeight;

    @ApiModelProperty("起始价格 - 起始价格/首重")
    private BigDecimal startPrice;

    @ApiModelProperty("续重重量 - 续费重量")
    private BigDecimal deltaWeightPerStage;

    @ApiModelProperty("续重价格 - 续费价格")
    private BigDecimal deltaChargePerStage;

    @ApiModelProperty("最小价格")
    private BigDecimal minPrice;

    @ApiModelProperty("最低价格 - 最高价格")
    private BigDecimal maxPrice;

    @ApiModelProperty("折扣")
    private BigDecimal percentage;

    @ApiModelProperty("报价类型 - 计价类型")
    private String chargeRuleType;

}
