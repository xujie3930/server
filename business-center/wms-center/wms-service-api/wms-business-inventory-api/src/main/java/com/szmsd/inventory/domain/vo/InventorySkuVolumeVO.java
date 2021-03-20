package com.szmsd.inventory.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventorySkuVolumeVO", description = "库存SKU体积")
public class InventorySkuVolumeVO {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "SKU体积")
    private List<SkuVolumeVO> skuVolumes;

}
