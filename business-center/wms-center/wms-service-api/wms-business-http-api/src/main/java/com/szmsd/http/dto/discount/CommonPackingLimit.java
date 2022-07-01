package com.szmsd.http.dto.discount;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CommonPackingLimit {

    @ApiModelProperty("长")
    private int length;

    @ApiModelProperty("宽")
    private int width;

    @ApiModelProperty("高")
    private int height;

    @ApiModelProperty("长度单位")
    private String lengthUnit;
}
