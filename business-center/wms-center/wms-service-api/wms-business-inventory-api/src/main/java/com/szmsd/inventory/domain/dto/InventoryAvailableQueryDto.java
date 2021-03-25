package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@Data
public class InventoryAvailableQueryDto implements Serializable {

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "sku")
    private String sku;
}
