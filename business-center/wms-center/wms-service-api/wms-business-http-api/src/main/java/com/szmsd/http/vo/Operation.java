package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "Operation")
public class Operation {

    @ApiModelProperty(value = "operator")
    private Operator operator;

    @ApiModelProperty(value = "time")
    private String time;

}
