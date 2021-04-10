package com.szmsd.inventory.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@Data
public class InventoryVO {

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "总库存")
    private Integer totalInventory;

    @ApiModelProperty(value = "可用库存")
    private Integer availableInventory;

    public void subAvailableInventory(int num) {
        this.availableInventory = this.availableInventory - num;
    }
}
