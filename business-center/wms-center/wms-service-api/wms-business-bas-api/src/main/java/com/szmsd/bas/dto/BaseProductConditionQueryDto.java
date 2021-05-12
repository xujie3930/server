package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseProductConditionQueryDto {

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "卖家编码")
    private String sellerCode;

    @ApiModelProperty(value = "仓库编码")
    private List<String> skus;
}
