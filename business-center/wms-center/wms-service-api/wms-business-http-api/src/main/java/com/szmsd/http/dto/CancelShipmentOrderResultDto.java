package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-12 19:38
 */
@Data
@ApiModel(value = "CancelShipmentOrderResultDto", description = "CancelShipmentOrderResultDto对象")
public class CancelShipmentOrderResultDto implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    @ApiModelProperty(value = "主挂号")
    private String trackingNumber;

    @ApiModelProperty(value = "Error")
    private ErrorDto error;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;
}
