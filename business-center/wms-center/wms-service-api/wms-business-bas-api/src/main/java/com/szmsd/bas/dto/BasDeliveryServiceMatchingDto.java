package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BasDeliveryServiceMatchingDto {


    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "目的国家编码")
    private String countryCode;

    @ApiModelProperty(value = "sku")
    private List<String> skuList;

}
