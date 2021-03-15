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
@ApiModel(value = "CancelShipmentOrderCommandDto", description = "CancelShipmentOrderCommandDto对象")
public class CancelShipmentOrderCommandDto implements Serializable {

    @ApiModelProperty(value = "请求唯一标识，可以使用GUID")
    private String referenceNumber;

    @ApiModelProperty(value = "待取消订单号")
    private CancelShipmentOrderDto cancelShipmentOrders;
}
