package com.szmsd.bas.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseProductExportDto {
    @ApiModelProperty(value = "序号")
    @Excel(name = "序号")
    private Integer no;

    @ApiModelProperty(value = "英文申报品名")
    @Excel(name = "英文申报品名")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "sku编号")
    private String code;

    @ApiModelProperty(value = "重量 g")
    @Excel(name = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "长 cm")
    @Excel(name = "长 cm")
    private Double length;

    @ApiModelProperty(value = "宽 cm")
    @Excel(name = "宽 cm")
    private Double width;

    @ApiModelProperty(value = "高 cm")
    @Excel(name = "高 cm")
    private Double height;

    @ApiModelProperty(value = "是否仓库测量")
    private Boolean warehouseAcceptance;

    @ApiModelProperty(value = "是否仓库测量")
    @Excel(name = "是否仓库测量")
    private String warehouseAcceptanceValue;

    @ApiModelProperty(value = "产品属性")
    @Excel(name = "产品属性")
    private String productAttributeName;

    @ApiModelProperty(value = "申报价值")
    @Excel(name = "申报价值")
    private Double declaredValue;

    @ApiModelProperty(value = "中文申报品名")
    @Excel(name = "中文申报品名")
    private String productNameChinese;

    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注" ,width = 30)
    private String remark;
}
