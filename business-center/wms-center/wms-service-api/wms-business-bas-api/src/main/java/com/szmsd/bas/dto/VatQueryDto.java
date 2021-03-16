package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class VatQueryDto {

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String countryCode;
    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

}
