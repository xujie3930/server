package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(value = "装箱信息", description = "DelOutboundPackingDto对象")
public class DelOutboundPackingDto implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @ApiModelProperty(value = "明细")
    private List<DelOutboundPackingDetailDto> details;
}
