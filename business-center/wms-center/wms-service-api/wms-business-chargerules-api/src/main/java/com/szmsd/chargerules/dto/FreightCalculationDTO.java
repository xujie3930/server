package com.szmsd.chargerules.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "FreightCalculationDTO", description = "运费测算")
public class FreightCalculationDTO {

    @ApiModelProperty(value = "客户代码")
    private String clientCode;

    @ApiModelProperty(value = "目的地国家")
    private String country;

    @ApiModelProperty(value = "处理点（匹配仓库）")
    private String dealPoint;

    @ApiModelProperty(value = "重量（KG）")
    private BigDecimal weight;

    @ApiModelProperty(value = "包裹数量")
    private Integer quantity;

    @ApiModelProperty(value = "邮编")
    private String postCode;

    @ApiModelProperty(value = "货物类型")
    private String shipmentType;

    @ApiModelProperty(value = "长")
    private BigDecimal extent;

    @ApiModelProperty(value = "宽")
    private BigDecimal width;

    @ApiModelProperty(value = "高")
    private BigDecimal height;

}
