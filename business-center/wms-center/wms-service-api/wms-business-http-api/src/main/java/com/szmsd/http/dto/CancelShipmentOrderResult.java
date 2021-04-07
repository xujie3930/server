package com.szmsd.http.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 取消订单
 *
 * @author zhangyuyuan
 * @date 2021-04-07 11:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelShipmentOrderResult implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    @ApiModelProperty(value = "主挂号")
    private String trackingNumber;

    @ApiModelProperty(value = "异常信息的封装")
    private ErrorDto error;

    @ApiModelProperty(value = "是否成功")
    private boolean success;
}
