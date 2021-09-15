package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "DelOutboundBatchPackingRequest", description = "DelOutboundBatchPackingRequest对象")
public class DelOutboundBatchPackingDetailRequest implements Serializable {

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "指定编码")
    private String newLabelCode;
}
