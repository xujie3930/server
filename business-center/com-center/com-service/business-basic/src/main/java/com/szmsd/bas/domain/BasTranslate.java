package com.szmsd.bas.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "BasTranslate对象", description = "中英文转换表")
public class BasTranslate {

    @ApiModelProperty(value = "主键id")
    private Integer id;

    @ApiModelProperty(value = "中文")
    private String zhName;

    @ApiModelProperty(value = "英文")
    private String enName;


}