package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseProductQueryDto {

    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "产品编码")
    private String code;

    @ApiModelProperty(value = "sku/包材")
    @Excel(name = "sku/包材")
    private String category;


    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "产品属性编号")
    @Excel(name = "产品属性编号")
    private String productAttribute;

    @ApiModelProperty(value = "是否激活")
    private Boolean isActive;

}
