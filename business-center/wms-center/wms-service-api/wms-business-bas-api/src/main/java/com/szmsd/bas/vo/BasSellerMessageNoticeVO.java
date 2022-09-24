package com.szmsd.bas.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BasSellerMessageNoticeVO {

    @ApiModelProperty(value = "异常未处理数")
    private Integer exceptionUnhandledNumber;

    @ApiModelProperty(value = "消息未读数")
    private Integer messageUnhandledNumber;

    @ApiModelProperty(value = "总数")
    private Integer totalNumber;
}
