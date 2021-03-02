package com.szmsd.bas.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenanze
 * @date 2020-06-13
 * @description
 */
@Data
public class BasDto {


    @ApiModelProperty(value = "开始时间")
    private String startTime;


    @ApiModelProperty(value = "结束时间")
    private String endTime;


}
