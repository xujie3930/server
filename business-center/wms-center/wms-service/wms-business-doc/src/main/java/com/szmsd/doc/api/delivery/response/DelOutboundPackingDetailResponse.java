package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DelOutboundPackingDetailResponse", description = "DelOutboundPackingDetailResponse对象")
public class DelOutboundPackingDetailResponse {

    @ApiModelProperty(value = "SKU")
    private String sku;

    @ApiModelProperty(value = "数量")
    private Long qty;

}
