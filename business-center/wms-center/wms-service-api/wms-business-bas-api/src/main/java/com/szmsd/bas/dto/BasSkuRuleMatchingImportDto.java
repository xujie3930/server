package com.szmsd.bas.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BasSkuRuleMatchingImportDto {


    @ApiModelProperty(value = "源系统sku")
    @Excel(name = "Shopify销售SKU",type = Excel.Type.IMPORT)
    @ExcelProperty(index = 0)
    private String sourceSku;

    @ApiModelProperty(value = "OMS SKU")
    @Excel(name = "OMS SKU",type = Excel.Type.IMPORT)
    @ExcelProperty(index = 1)
    private String omsSku;


}
