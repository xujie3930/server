package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventorySkuVolumeQueryDTO", description = "查询库存SKU体积")
public class InventorySkuVolumeQueryDTO {

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

}
