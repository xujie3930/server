package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:16
 */
@Data
@ApiModel(value = "ShipmentMultiboxrelationRequestDto", description = "ShipmentMultiboxrelationRequestDto对象")
public class ShipmentMultiboxrelationRequestDto implements Serializable {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "明细")
    private List<ShipmentMultiboxrelationDetailDto> details;
}
