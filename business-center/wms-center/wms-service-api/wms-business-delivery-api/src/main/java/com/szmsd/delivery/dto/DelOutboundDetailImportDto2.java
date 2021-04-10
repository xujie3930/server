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
public class DelOutboundDetailImportDto2 implements Serializable {

    @ApiModelProperty(value = "订单顺序")
    private Integer sort;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Integer qty;
}
