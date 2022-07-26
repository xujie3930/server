package com.szmsd.inventory.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.language.annotation.FieldJsonI18n;
import com.szmsd.common.core.language.constant.RedisLanguageTable;
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
public class InventorySkuVO {

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库")
    private String warehouseCode;

    @ApiModelProperty(value = "所属仓库")
    @AutoFieldI18n
//    @FieldJsonI18n(type = RedisLanguageTable.BAS_WAREHOUSE)
    private String warehouseName;

    @ApiModelProperty(value = "sku")
    @Excel(name = "SKU")
    private String sku;

    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称")
    private String skuName;

    @ApiModelProperty(value = "产品类别")
    @Excel(name = "类别")
    private String skuCategoryName;

    @ApiModelProperty(value = "总库存")
    @Excel(name = "总库存")
    private Integer totalInventory;

    @ApiModelProperty(value = "可用库存")
    @Excel(name = "可用库存")
    private Integer availableInventory;

    @ApiModelProperty(value = "冻结库存")
    private Integer freezeInventory;

    @ApiModelProperty(value = "总入库")
    @Excel(name = "总入库数量")
    private Integer totalInbound;

    @ApiModelProperty(value = "总出库")
    @Excel(name = "总出库数量")
    private Integer totalOutbound;

    @ApiModelProperty(value = "重量")
    @Excel(name = "重量")
    private BigDecimal skuWeight;

    @ApiModelProperty(value = "长")
    @Excel(name = "长")
    private Integer skuLength;

    @ApiModelProperty(value = "宽")
    @Excel(name = "宽")
    private Integer skuWidth;

    @ApiModelProperty(value = "高")
    @Excel(name = "高")
    private Integer skuHeight;
    @AutoFieldI18n
    @ApiModelProperty(value = "产品属性")
    @Excel(name = "产品属性")
    private String skuPropertyName;

    @ApiModelProperty(value = "申报价值")
    @Excel(name = "申报价值")
    private String skuDeclaredValue;

    @ApiModelProperty(value = "申报品名")
    @Excel(name = "申报品名")
    private String skuDeclaredName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "关联单号")
    private String relevanceNumber;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String cusCode;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "最后入库时间")
    private Date lastInboundTime;

}
