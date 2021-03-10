package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:14
 */
@Data
@ApiModel(value = "ShipmentLabelChangeRequestDto", description = "ShipmentLabelChangeRequestDto对象")
public class ShipmentLabelChangeRequestDto implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "标签类型 有3种\n" +
            "运单标签：ShipmentLabel\n" +
            "发票：ShipmentInvoice\n" +
            "其他：Other")
    private String labelType;

    @ApiModelProperty(value = "标签（Base64String 格式")
    private String label;
}
