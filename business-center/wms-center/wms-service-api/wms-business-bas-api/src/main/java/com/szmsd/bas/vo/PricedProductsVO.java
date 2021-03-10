package com.szmsd.bas.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedProductsVO", description = "运费测算返还参数")
public class PricedProductsVO {

    @ApiModelProperty(value = "渠道名称")
    private String shippingChannel;

    @ApiModelProperty(value = "计费重量（KG）")
    private String chargeableWeight;

    @ApiModelProperty(value = "计费类型")
    private String chargeableType;

    @ApiModelProperty(value = "预估总费用")
    private String totalAmount;

    @ApiModelProperty(value = "费用明细")
    private String detail;

    @ApiModelProperty(value = "预估计实效")
    private String timeliness;

}
