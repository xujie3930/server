package com.szmsd.doc.api.sub.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "子类别", description = "子类别")
public class SubResponse {

    @ApiModelProperty("主类别code")
    private String mainCode;

    @ApiModelProperty("主类别名称")
    private String mainName;

    @ApiModelProperty("子类别code")
    private String subCode;

    @ApiModelProperty("子类别名称")
    private String subName;

    @ApiModelProperty("子类别名称-英文")
    private String subNameEn;

    @ApiModelProperty("子类别值")
    private String subValue;

}
