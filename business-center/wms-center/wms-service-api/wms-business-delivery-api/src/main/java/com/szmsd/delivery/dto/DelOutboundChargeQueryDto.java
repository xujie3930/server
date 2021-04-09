package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "DelOutboundChargeQueryDto", description = "DelOutboundChargeQueryDto对象")
public class DelOutboundChargeQueryDto implements Serializable {

    @ApiModelProperty(value = "处理号/挂号/出库单号")
    private String no;

    @ApiModelProperty(value = "物流服务（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @ApiModelProperty(value = "下单时间开始")
    private String orderTimeStart;

    @ApiModelProperty(value = "下单时间结束")
    private String orderTimeEnd;

}
