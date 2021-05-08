package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryInspectionDetailsDTO {

    @ApiModelProperty(value = "客户代码")
    private String sku;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseCodeName;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "客户名称")
    private String customName;

    public InventoryInspectionDetailsDTO(String sku, String warehouseCode, String customCode) {
        this.sku = sku;
        this.warehouseCode = warehouseCode;
        this.customCode = customCode;
    }
}
