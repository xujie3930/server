package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 9:46
 */
@Data
@ApiModel(value = "DelOutboundShipmentResponse", description = "DelOutboundShipmentResponse对象")
public class DelOutboundShipmentResponse {

    @ApiModelProperty(value = "订单号", dataType = "String", example = "D001")
    private String orderNo;

    @ApiModelProperty(value = "挂号", dataType = "String")
    private String trackingNo;

    @ApiModelProperty(value = "状态", dataType = "Boolean")
    private Boolean status;

    @ApiModelProperty(value = "消息", dataType = "String")
    private String message;
}
