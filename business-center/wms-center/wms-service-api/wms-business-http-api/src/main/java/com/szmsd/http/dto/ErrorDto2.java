package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-27 14:39
 */
@Data
@ApiModel(value = "ErrorDto2", description = "ErrorDto2对象")
public class ErrorDto2 implements Serializable {

    @ApiModelProperty(value = "错误类型")
    private String Sn;

    @ApiModelProperty(value = "错误代码")
    private String Code;

    @ApiModelProperty(value = "封装错误信息")
    private String Message;

}
