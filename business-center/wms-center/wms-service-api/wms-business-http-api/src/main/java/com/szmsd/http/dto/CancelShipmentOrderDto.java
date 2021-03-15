package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-12 19:36
 */
@Data
@ApiModel(value = "CancelShipmentOrderDto", description = "CancelShipmentOrderDto对象")
public class CancelShipmentOrderDto implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    @ApiModelProperty(value = "挂号")
    private String trackingNumber;
}
