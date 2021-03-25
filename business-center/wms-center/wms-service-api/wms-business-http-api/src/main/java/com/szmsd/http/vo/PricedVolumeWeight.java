package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedVolumeWeight", description = "体积重")
public class PricedVolumeWeight {

    @ApiModelProperty("体积重系数")
    private Integer volumeWeightStandards;

    @ApiModelProperty("计泡系数")
    private BigDecimal volumeWeightReduce;

    @ApiModelProperty
    private PackageLimit packageLimit;

    @ApiModelProperty("体积重计费类型")
    private String volumeWeightType;

}
