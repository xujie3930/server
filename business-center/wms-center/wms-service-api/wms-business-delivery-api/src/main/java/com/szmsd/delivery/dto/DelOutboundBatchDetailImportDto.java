package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 */
@Data
@ApiModel(value = "DelOutboundBatchDetailImportDto", description = "DelOutboundBatchDetailImportDto对象")
public class DelOutboundBatchDetailImportDto implements Serializable {

    @ApiModelProperty(value = "订单顺序")
    private Integer sort;

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;

}
