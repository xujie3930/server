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
@ApiModel(value = "ErrorDto", description = "ErrorDto对象")
public class ErrorDto implements Serializable {

    @ApiModelProperty(value = "封装错误信息")
    private String message;

    @ApiModelProperty(value = "错误类型")
    private String errorType;

    @ApiModelProperty(value = "错误代码")
    private String errorCode;
}
