package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:12
 */
@Data
@ApiModel(value = "ShipmentTrackingChangeRequestDto", description = "ShipmentTrackingChangeRequestDto对象")
public class ShipmentTrackingChangeRequestDto implements Serializable {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "挂号")
    private String trackingNo;

    @ApiModelProperty(value = "发货规则(为空代表不修改)")
    private String shipmentRule;

    @ApiModelProperty(value = "装箱规则(为空代表不修改)")
    private String packingRule;
}
