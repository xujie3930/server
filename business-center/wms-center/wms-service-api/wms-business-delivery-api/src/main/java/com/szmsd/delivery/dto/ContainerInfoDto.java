package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author zhangyuyuan
 * @date 2021-03-08 19:05
 */
@Data
@ApiModel(value = "ContainerInfoDto", description = "ContainerInfoDto对象")
public class ContainerInfoDto implements Serializable {

    @ApiModelProperty(value = "容器号")
    private String containerCode;

    @ApiModelProperty(value = "长 CM")
    private Double length;

    @ApiModelProperty(value = "宽 CM")
    private Double width;

    @ApiModelProperty(value = "高 CM")
    private Double height;

    @ApiModelProperty(value = "重量 g")
    private Double weight;

    @ApiModelProperty(value = "包材类型")
    private String packingMaterial;

    @ApiModelProperty(value = "箱明细")
    private List<ContainerDetailDto> containerDetailList;
}
