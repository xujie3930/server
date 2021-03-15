package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventorySkuQueryDTO", description = "InventorySkuQueryDTO库存管理 - 查询入参")
public class InventorySkuQueryDTO {

    @ApiModelProperty(value = "主键ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "产品属性")
    private String skuPropertyCode;

    @ApiModelProperty(value = "sku多个用\",\"隔开")
    private String sku;

    @ApiModelProperty(value = "sku集合")
    private List<String> skuList;

    @ApiModelProperty(value = "申报品名")
    private String skuDeclaredName;

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

}
