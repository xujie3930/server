package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasSkuRuleMatchingDto {

    @ApiModelProperty(value = "系统类型0.Shopify")
    private String systemType;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;


    @ApiModelProperty(value = "源系统sku")
    private List<String> sourceSkuList;

}
