package com.szmsd.ec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 发货结果回调参数
 */
@Data
public class CallbackShippingResultDTO {

    @NotBlank(message = "电商订单号不能为空")
    @ApiModelProperty("电商订单号")
    private String orderNo;

    @NotBlank(message = "物流单号不能为空")
    @ApiModelProperty("物流单号")
    private String transferNumber;

    @NotBlank(message = "承运商不能为空")
    @ApiModelProperty("承运商")
    private String logisticsRouteId;
}
