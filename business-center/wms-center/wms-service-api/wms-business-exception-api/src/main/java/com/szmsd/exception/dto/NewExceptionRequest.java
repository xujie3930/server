package com.szmsd.exception.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewExceptionRequest {
    @ApiModelProperty(value = "操作人姓名")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private String operateOn;

    @ApiModelProperty(value = "仓库编码")
    @NotBlank(message = "仓库编码不能为空")
    private String warehouseCode;

    @ApiModelProperty(value = "单号")
    @NotBlank(message = "单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "异常号")
    @NotBlank(message = "异常号不能为空")
    private String exceptionNo;

    @ApiModelProperty(value = "单类型")
    @NotBlank(message = "单类型不能为空")
    private String orderType;

    @ApiModelProperty(value = "异常类型")
    @NotBlank(message = "异常类型不能为空")
    private String exceptionType;

    @ApiModelProperty(value = "是否系统自动创建")
    @NotNull(message = "异常类型不能为空")
    private Boolean isAutoCreated;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "客户编码")
    @NotBlank(message = "客户编码不能为空")
    private String sellerCode;


}
