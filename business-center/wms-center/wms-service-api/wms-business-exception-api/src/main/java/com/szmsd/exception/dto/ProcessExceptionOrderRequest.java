package com.szmsd.exception.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProcessExceptionOrderRequest {
    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "订单号")
    private String orderNo;
}
