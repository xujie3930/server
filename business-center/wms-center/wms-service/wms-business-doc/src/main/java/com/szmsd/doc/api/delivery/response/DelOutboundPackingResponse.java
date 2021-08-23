package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DelOutboundPackingResponse", description = "DelOutboundPackingResponse对象")
public class DelOutboundPackingResponse {

    @ApiModelProperty(value = "数量")
    private Long qty;

    @ApiModelProperty(value = "箱号")
    private String packingNo;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "包材类型")
    private String packingMaterial;
}
