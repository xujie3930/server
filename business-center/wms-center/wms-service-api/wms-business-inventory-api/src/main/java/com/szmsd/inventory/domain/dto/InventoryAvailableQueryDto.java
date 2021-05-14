package com.szmsd.inventory.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 15:06
 */
@Data
public class InventoryAvailableQueryDto implements Serializable {

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "sku")
    private String eqSku;

    @ApiModelProperty(value = "skus")
    private List<String> skus;

    @ApiModelProperty(value = "查询类型，1可用库存为0时不查询。2可用库存为0时查询。默认1")
    private Integer queryType = 1;

    private String querySku;
}
