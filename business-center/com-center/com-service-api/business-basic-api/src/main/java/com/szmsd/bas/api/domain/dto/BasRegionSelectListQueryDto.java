package com.szmsd.bas.api.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2020-12-16 016 9:15
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "BasRegionSelectListQueryDto", description = "BasRegionSelectListQueryDto信息")
public class BasRegionSelectListQueryDto implements Serializable {

    @ApiModelProperty(value = "父节点id")
    private Long pId;

    @ApiModelProperty(value = "地址类别:1国家 2省份 3市 4区 5街道")
    private Integer type;

    @ApiModelProperty(value = "name")
    private String name;

    @ApiModelProperty(value = "地址简码")
    private String addressCode;

    @ApiModelProperty(value = "英文名")
    private String enName;
}
