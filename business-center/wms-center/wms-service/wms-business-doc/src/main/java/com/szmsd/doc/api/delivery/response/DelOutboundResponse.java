package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DelOutboundResponse", description = "DelOutboundResponse对象")
public class DelOutboundResponse {

    @ApiModelProperty(value = "订单号", dataType = "String", example = "D001")
    private String orderNo;

    @ApiModelProperty(value = "挂号", dataType = "String")
    private String trackingNo;

    @ApiModelProperty(value = "状态", dataType = "Boolean")
    private Boolean status;

    @ApiModelProperty(value = "消息", dataType = "String")
    private String message;
}
