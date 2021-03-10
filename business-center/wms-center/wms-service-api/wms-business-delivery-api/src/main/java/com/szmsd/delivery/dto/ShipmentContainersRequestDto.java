package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 19:04
 */
@Data
@ApiModel(value = "ShipmentContainersRequestDto", description = "ShipmentContainersRequestDto对象")
public class ShipmentContainersRequestDto implements Serializable {

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "装箱列表")
    private List<ContainerInfoDto> containerList;
}
