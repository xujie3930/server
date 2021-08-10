package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-08-03 10:31
 */
@Data
@ApiModel(value = "DelOutboundAddResponse", description = "DelOutboundAddResponse对象")
public class DelOutboundAddResponse implements Serializable {

    @ApiModelProperty(value = "ID", dataType = "Long", example = "157")
    private Long id;

    @ApiModelProperty(value = "订单号", dataType = "String", example = "D001")
    private String orderNo;

    @ApiModelProperty(value = "挂号", dataType = "String")
    private String trackingNo;

    @ApiModelProperty(value = "状态", dataType = "Boolean")
    private Boolean status;

    @ApiModelProperty(value = "消息", dataType = "String")
    private String message;
}
