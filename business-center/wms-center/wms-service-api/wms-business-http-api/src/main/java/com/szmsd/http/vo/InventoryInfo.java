package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "InventoryInfo", description = "WMS - InventoryInfo")
public class InventoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 仓库代码 **/
    private String warehouseCode;

    /** SKU **/
    private String sku;

    /** 数量 **/
    private Integer qty;

}
