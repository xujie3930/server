package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "ResponseVO2", description = "ResponseVO2对象")
public class ResponseVO2<T> implements Serializable {

    @ApiModelProperty(value = "是否执行成功")
    private Boolean success;

    @ApiModelProperty(value = "返回消息")
    private String message;

    @ApiModelProperty(value = "记录总数")
    private Integer recordCount;

    @ApiModelProperty(value = "数据")
    private List<T> recordData;

}
