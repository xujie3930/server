package com.szmsd.inventory.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.plugin.annotation.AutoFieldI18n;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventorySkuVO", description = "InventorySkuVO库存管理列表")
public class InventorySkuEnVO {

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "Warehouse")
    private String warehouseCode;


    @ApiModelProperty(value = "客户代码")
    @Excel(name = "Customer Code")
    private String cusCode;



    @ApiModelProperty(value = "sku")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "产品名称")
    @Excel(name = "Product name")
    private String skuName;

    @ApiModelProperty(value = "产品类别")
    @Excel(name = "Category")
    private String skuCategoryName;

    @ApiModelProperty(value = "总库存")
    @Excel(name = "Total inventory")
    private Integer totalInventory;

    @ApiModelProperty(value = "可用库存")
    @Excel(name = "Available stock")
    private Integer availableInventory;

    @ApiModelProperty(value = "总入库")
    @Excel(name = "Total quantity in storage")
    private Integer totalInbound;

    @ApiModelProperty(value = "总出库")
    @Excel(name = "Total outbound quantity")
    private Integer totalOutbound;

    @ApiModelProperty(value = "重量")
    @Excel(name = "Weight（g）")
    private BigDecimal skuWeight;

    @ApiModelProperty(value = "长")
    @Excel(name = "Length（cm）")
    private Integer skuLength;

    @ApiModelProperty(value = "宽")
    @Excel(name = "Width（cm）")
    private Integer skuWidth;

    @ApiModelProperty(value = "高")
    @Excel(name = "Height （cm）")
    private Integer skuHeight;
    @AutoFieldI18n
    @ApiModelProperty(value = "产品属性")
    @Excel(name = "Product Attribute")
    private String skuPropertyName;

    @ApiModelProperty(value = "申报价值")
    @Excel(name = "Declared Value")
    private String skuDeclaredValue;

    @ApiModelProperty(value = "申报品名")
    @Excel(name = "Declare Commodity Name")
    private String skuDeclaredName;

}
