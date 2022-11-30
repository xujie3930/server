package com.szmsd.exception.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ExceptionInfoStateDto implements Serializable {

    @ApiModelProperty(value = "类型")
    private String state;

    @ApiModelProperty(value = "单号")
    private List<String> orderNos;
}
