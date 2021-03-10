package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedProductsQueryDTO", description = "运费测算入参")
public class PricedProductsDTO {

    @ApiModelProperty(value = "国家（匹配国家库）")
    private String country;

    @ApiModelProperty(value = "重量（KG）")
    private BigDecimal weight;

    @ApiModelProperty(value = "包裹数量")
    private Integer quantity;

    @ApiModelProperty(value = "品名")
    private Integer goodsName;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "货物包装规格")
    private String rule;

    @ApiModelProperty(value = "处理点（匹配仓库）")
    private String dealPoint;

    @ApiModelProperty(value = "包裹尺寸")
    private String packageSize;

}
