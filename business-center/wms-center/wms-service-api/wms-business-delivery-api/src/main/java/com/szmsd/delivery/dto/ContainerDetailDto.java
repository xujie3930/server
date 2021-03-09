package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 19:06
 */
@Data
@ApiModel(value = "ContainerDetailDto", description = "ContainerDetailDto对象")
public class ContainerDetailDto implements Serializable {

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "qty")
    private Integer qty;
}
