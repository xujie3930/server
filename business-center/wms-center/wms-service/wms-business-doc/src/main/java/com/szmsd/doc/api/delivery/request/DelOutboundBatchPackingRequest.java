package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "DelOutboundBatchPackingRequest", description = "DelOutboundBatchPackingRequest对象")
public class DelOutboundBatchPackingRequest implements Serializable {

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

    @Valid
    @ApiModelProperty(value = "明细")
    private List<DelOutboundBatchPackingDetailRequest> details;
}
