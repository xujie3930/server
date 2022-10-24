    package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-30 11:38
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ShipmentOrderResult", description = "ShipmentOrderResult对象")
public class ShipmentOrderSubmission implements Serializable {

    @ApiModelProperty(value = "业务流水号")
    private String referenceNumber;

    @ApiModelProperty(value = "订单")
    private ShipmentOrderResult submitOrder;

}
