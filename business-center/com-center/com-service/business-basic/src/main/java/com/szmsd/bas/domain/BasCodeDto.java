package com.szmsd.bas.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-29 10:12
 * @Description
 */
@Data
@Accessors(chain = true)
public class BasCodeDto {

    @ApiModelProperty(value = "应用编号")
    private String appId;

    @ApiModelProperty(value = "流水号编码")
    private String code;

    @ApiModelProperty(value = "前缀")
    private String prefix;

    @ApiModelProperty(value = "单据类型")
    private String bizType;

    @ApiModelProperty(value = "后缀")
    private String suffix;

    @ApiModelProperty(value = "要生成的数量")
    private int count=1;

    private int status;
}
