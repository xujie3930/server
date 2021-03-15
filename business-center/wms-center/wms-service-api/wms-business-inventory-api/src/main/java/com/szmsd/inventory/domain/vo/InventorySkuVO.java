package com.szmsd.inventory.domain.vo;

import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventorySkuVO", description = "InventorySkuVO库存管理列表")
public class InventorySkuVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "产品名称")
    private String skuName;

    @ApiModelProperty(value = "产品类别")
    private String skuCategoryName;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "所属仓库")
    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    private String warehouseName;

    @ApiModelProperty(value = "总库存")
    private Integer totalInventory;

    @ApiModelProperty(value = "可用库存")
    private Integer availableInventory;

    @ApiModelProperty(value = "冻结库存")
    private Integer freezeInventory;

    @ApiModelProperty(value = "总入库")
    private Integer totalInbound;

    @ApiModelProperty(value = "总出库")
    private Integer totalOutbound;

    @ApiModelProperty(value = "重量")
    private BigDecimal skuWeight;

    @ApiModelProperty(value = "长")
    private Integer skuLength;

    @ApiModelProperty(value = "宽")
    private Integer skuWidth;

    @ApiModelProperty(value = "高")
    private Integer skuHeight;

    @ApiModelProperty(value = "产品属性")
    private String skuPropertyName;

    @ApiModelProperty(value = "申报价值")
    private String skuDeclaredValue;

    @ApiModelProperty(value = "申报品名")
    private String skuDeclaredName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

}
