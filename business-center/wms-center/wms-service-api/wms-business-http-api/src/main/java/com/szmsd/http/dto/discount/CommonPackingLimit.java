package com.szmsd.http.dto.discount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CommonPackingLimit {

    @ApiModelProperty("长")
    private BigDecimal length;

    @ApiModelProperty("宽")
    private BigDecimal width;

    @ApiModelProperty("高")
    private BigDecimal height;

    @ApiModelProperty("长度单位")
    private String lengthUnit;
}
