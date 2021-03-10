package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "Weight")
public class Weight {

    @ApiModelProperty
    private BigDecimal value;

    @ApiModelProperty
    private String unit;

}
