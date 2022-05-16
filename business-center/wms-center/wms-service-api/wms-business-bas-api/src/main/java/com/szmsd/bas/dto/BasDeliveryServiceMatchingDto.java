package com.szmsd.bas.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasDeliveryServiceMatchingDto {


    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "sku")
    private List<String> skuList;

}
