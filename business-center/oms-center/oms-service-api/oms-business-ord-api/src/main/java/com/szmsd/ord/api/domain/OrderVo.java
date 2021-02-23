package com.szmsd.ord.api.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-13 11:05
 * @Description
 */
@Data
public class OrderVo extends Order {

    @ApiModelProperty(value = "错误信息")
    private String errorMsg;

    @ApiModelProperty(value = "分拣码")
    private String sortingCode;


    @ApiModelProperty(value = "用户id")
    private String userId;
}
