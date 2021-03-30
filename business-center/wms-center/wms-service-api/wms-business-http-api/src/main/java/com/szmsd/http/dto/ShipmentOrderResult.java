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
public class ShipmentOrderResult implements Serializable {

    @ApiModelProperty(value = "业务流水号")
    private String referenceNumber;

    @ApiModelProperty(value = "订单号")
    private String orderNumber;

    @ApiModelProperty(value = "包裹信息")
    private List<PackageResult> packages;

    @ApiModelProperty(value = "主挂号")
    private String mainTrackingNumber;

    @ApiModelProperty(value = "订单标签")
    private String orderLabelUrl;

    @ApiModelProperty(value = "订单发票标签")
    private String orderInvoiceUrl;

    @ApiModelProperty(value = "异常信息的封装")
    private ErrorDto error;

    @ApiModelProperty(value = "是否成功")
    private Boolean success;
}
