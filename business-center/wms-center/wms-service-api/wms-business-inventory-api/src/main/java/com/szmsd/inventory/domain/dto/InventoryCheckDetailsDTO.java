package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class InventoryCheckDetailsDTO {

    @ApiModelProperty(value = "客户代码")
    private String sku;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "客户代码")
    private String customCode;


}
