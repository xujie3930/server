package com.szmsd.exception.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ProcessExceptionRequest {
    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "目的仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "异常号")
    @NotBlank(message = "异常号不能为空")
    private String exceptionNo;

    @ApiModelProperty(value = "备注")
    private String remark;
}
