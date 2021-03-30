package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "", description = "")
public class ChargeModel {

    @ApiModelProperty
    private String totalAmount;

    @ApiModelProperty
    private List<PricingChargeItem> details;
}
