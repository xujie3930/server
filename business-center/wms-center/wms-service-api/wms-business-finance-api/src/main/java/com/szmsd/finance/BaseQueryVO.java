package com.szmsd.finance;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BaseQueryVO implements Serializable {

    @ApiModelProperty(value = "当前起始页索引")
    private int pageNum = 1;

    @ApiModelProperty(value = "每页显示记录数")
    private int pageSize = 20;



}
