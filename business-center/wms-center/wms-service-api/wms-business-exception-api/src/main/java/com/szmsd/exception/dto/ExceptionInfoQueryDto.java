package com.szmsd.exception.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ExceptionInfoQueryDto {
    @ApiModelProperty(value = "异常类型")
    @Excel(name = "异常类型")
    private String exceptionType;

    @ApiModelProperty(value = "创建日期查询字段")
    @Excel(name = "创建日期查询字段")
    private String[] createTimes;

    @ApiModelProperty(value = "异常号list")
    @Excel(name = "异常号list")
    private List<String> orderNos;

    @ApiModelProperty(value = "单号list")
    @Excel(name = "单号list")
    private List<String> exceptionNos;

    @ApiModelProperty(value = "用户编码")
    @Excel(name = "用户编码")
    private String sellerCode;

    @ApiModelProperty(value = "类型")
    private String state;
}
