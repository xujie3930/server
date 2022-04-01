package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseProductEnExportDto {
    @ApiModelProperty(value = "序号")
    @Excel(name = "Serial \n" +
            "Number")
    private Integer no;


    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "Customer Code")
    private String sellerCode;

    @ApiModelProperty(value = "Declare Commodity Names")
    @Excel(name = "Declare Commodity Names")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "Sku Number")
    private String code;

    @ApiModelProperty(value = "重量 g")
    @Excel(name = "Weight (G)")
    private Double weight;

    @ApiModelProperty(value = "长 cm")
    @Excel(name = "Length (Cm)")
    private Double length;

    @ApiModelProperty(value = "宽 cm")
    @Excel(name = "Width (Cm)")
    private Double width;

    @ApiModelProperty(value = "高 cm")
    @Excel(name = "Height(Cm)")
    private Double height;

    @ApiModelProperty(value = "是否仓库测量")
    @Excel(name = "Warehouse Measurement")
    private String warehouseAcceptanceValue;

    @ApiModelProperty(value = "产品属性")
    @Excel(name = "Product Attribute ")
    private String productAttributeName;

    @ApiModelProperty(value = "申报价值")
    @Excel(name = "Ddeclared Value")
    private Double declaredValue;

    @ApiModelProperty(value = "中文申报品名")
    @Excel(name = "Declare Commodity Names(Chinese)")
    private String productNameChinese;


    @ApiModelProperty(value = "备注")
    @Excel(name = "Remarks" ,width = 30)
    private String remark;

}
