package com.szmsd.bas.api.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2020-12-16 016 9:15
 */
@Data
@ApiModel(value = "BasRegionSelectListVO", description = "BasRegionSelectListVO信息")
public class BasRegionSelectListVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "地址简码")
    private String addressCode;

    @ApiModelProperty(value = "英文名")
    private String enName;
}
