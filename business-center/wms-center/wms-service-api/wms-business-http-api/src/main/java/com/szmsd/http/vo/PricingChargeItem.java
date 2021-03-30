package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricingChargeItem", description = "")
public class PricingChargeItem {

    @ApiModelProperty
    private String description;

    @ApiModelProperty
    private BigDecimal price;

    @ApiModelProperty
    private String currency;

}
