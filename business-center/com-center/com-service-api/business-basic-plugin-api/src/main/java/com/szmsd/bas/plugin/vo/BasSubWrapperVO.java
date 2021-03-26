package com.szmsd.bas.plugin.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-26 11:01
 */
@Data
public class BasSubWrapperVO implements Serializable {

    @ApiModelProperty(value = "子类id")
    private String subCode;

    @ApiModelProperty(value = "子类别值")
    private String subValue;

    @ApiModelProperty(value = "子类名称（中）")
    private String subName;

    @ApiModelProperty(value = "子类名称（英）")
    private String subNameEn;
}
