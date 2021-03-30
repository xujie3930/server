package com.szmsd.http.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ExceptionProcessRequest {
    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "异常号")
    @Excel(name = "异常号")
    private String exceptionNo;

    @ApiModelProperty(value = "处理备注")
    @Excel(name = "处理备注")
    private String processRemark;

    @ApiModelProperty(value = "处理方式")
    @Excel(name = "处理方式")
    private String processType;
}
