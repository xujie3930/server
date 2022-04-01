package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:51
 */
@Data
@ApiModel(value = "DelOutboundDetailImportDto2", description = "DelOutboundDetailImportDto2对象")
public class DelOutboundDetailEnImportDto2 implements Serializable {

    @ApiModelProperty(value = "Order Row")
    private Integer sort;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "Qty")
    private Integer qty;
}
