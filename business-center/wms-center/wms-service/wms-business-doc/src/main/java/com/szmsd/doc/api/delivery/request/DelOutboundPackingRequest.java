package com.szmsd.doc.api.delivery.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel(value = "DelOutboundPackingRequest", description = "DelOutboundPackingRequest对象")
public class DelOutboundPackingRequest {

    @NotBlank(message = "订单号不能为空")
    @ApiModelProperty(value = "订单号")
    private String orderNo;
}
