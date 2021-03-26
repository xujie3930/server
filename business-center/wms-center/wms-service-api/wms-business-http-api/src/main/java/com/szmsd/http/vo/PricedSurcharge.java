package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedSurcharge", description = "附加费配置信息")
public class PricedSurcharge {

    @ApiModelProperty("偏远地区附加费模板")
    private String remoteModel;

    @ApiModelProperty("分区")
    private String zoneName;

    @ApiModelProperty("货币")
    private String currency;

    @ApiModelProperty
    private PackageLimit packageLimit;

    @ApiModelProperty
    private ChargeFormula formula;

    @ApiModelProperty("费用类型")
    private String chargeType;

    @ApiModelProperty("是否用于计算燃油附加费")
    private Boolean isCalcForFuelTax;

    @ApiModelProperty("费用计费方式")
    private String chargeCalculateMethod;

    @ApiModelProperty("Tag标签")
    private String tags;

}
