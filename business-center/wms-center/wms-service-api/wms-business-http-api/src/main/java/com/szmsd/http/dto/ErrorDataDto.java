package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-12 19:44
 */
@Data
@ApiModel(value = "ErrorDataDto", description = "ErrorDataDto对象")
public class ErrorDataDto implements Serializable {

    @ApiModelProperty(value = "错误表示")
    private String ticketId;

    @ApiModelProperty(value = "错误发生时间")
    private Date utcDateTime;

    @ApiModelProperty(value = "请求的URI")
    private String requestUri;

    @ApiModelProperty(value = "错误详情列表")
    private List<ErrorItemDto> errors;

}
