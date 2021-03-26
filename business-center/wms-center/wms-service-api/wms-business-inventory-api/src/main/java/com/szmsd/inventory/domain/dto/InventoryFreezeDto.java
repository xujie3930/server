package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@Data
public class InventoryFreezeDto implements Serializable {

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "冻结库存")
    private Integer freezeInventory;
}
