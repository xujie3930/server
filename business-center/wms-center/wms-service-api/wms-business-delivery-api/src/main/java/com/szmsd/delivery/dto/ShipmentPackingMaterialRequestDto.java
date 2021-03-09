package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 19:03
 */
@Data
@ApiModel(value = "ShipmentPackingMaterialRequestDto", description = "ShipmentPackingMaterialRequestDto对象")
public class ShipmentPackingMaterialRequestDto implements Serializable {

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "包材类型")
    private String packingMaterial;

}
