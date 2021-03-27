package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "PackageInfo")
public class PackageInfo {

    @ApiModelProperty
    private Weight weight;

    @ApiModelProperty
    private Packing packing;

    @ApiModelProperty(value = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "处理号")
    private String refNo;

    @ApiModelProperty(value = "申报价值")
    private BigDecimal declareValue;

}
