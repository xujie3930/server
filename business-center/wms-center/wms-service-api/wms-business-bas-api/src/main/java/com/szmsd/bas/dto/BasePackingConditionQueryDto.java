package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasePackingConditionQueryDto {

    @ApiModelProperty(value = "编码")
    private String code;
}
