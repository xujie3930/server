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
@ApiModel(value = "Packing")
public class Packing {

    @ApiModelProperty
    private BigDecimal length;

    @ApiModelProperty
    private BigDecimal width;

    @ApiModelProperty
    private BigDecimal height;

    @ApiModelProperty
    private String lengthUnit;

}
