package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "ChargeFormula", description = "费用计算公式")
public class ChargeFormula {

    @ApiModelProperty("起始价格/首重")
    private BigDecimal startPrice;

    @ApiModelProperty("续费重量")
    private BigDecimal deltaWeightPerStage;

    @ApiModelProperty("续费价格")
    private BigDecimal deltaChargePerStage;

    @ApiModelProperty("续数数量")
    private Integer detalNumberPerQuantity;

    @ApiModelProperty("续数价格")
    private BigDecimal detalChargePerQuantity;

    @ApiModelProperty("最小价格")
    private BigDecimal minPrice;

    @ApiModelProperty("最高价格")
    private BigDecimal maxPrice;

    @ApiModelProperty("折扣")
    private BigDecimal percentage;

    @ApiModelProperty("计价类型")
    private String chargeRuleType;

}
