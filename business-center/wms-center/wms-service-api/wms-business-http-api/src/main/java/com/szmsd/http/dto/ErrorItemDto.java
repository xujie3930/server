package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-12 19:45
 */
@Data
@ApiModel(value = "ErrorItemDto", description = "ErrorItemDto对象")
public class ErrorItemDto implements Serializable {

    @ApiModelProperty(value = "单据编号")
    private String sn;

    @ApiModelProperty(value = "错误代码")
    private String code;

    @ApiModelProperty(value = "错误信息")
    private String message;
}
