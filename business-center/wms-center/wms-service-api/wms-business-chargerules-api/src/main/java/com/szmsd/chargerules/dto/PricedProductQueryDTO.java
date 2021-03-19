package com.szmsd.chargerules.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedProductQueryDTO", description = "分页查询产品列表查询入参")
public class PricedProductQueryDTO {

    @ApiModelProperty(value = "产品代码")
    private String code;

}
