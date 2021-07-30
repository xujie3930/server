package com.szmsd.doc.api.delivery.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-07-29 16:29
 */
@Data
@ApiModel(value = "PricedProductResponse", description = "PricedProductResponse对象")
public class PricedProductResponse implements Serializable {

    @ApiModelProperty(value = "产品代码")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "服务商名称")
    private String terminalCarrier;

    @ApiModelProperty(value = "服务商名称 - 挂号服务")
    private String logisticsRouteId;

    @ApiModelProperty(value = "是否可下单")
    private Boolean inService;

    @ApiModelProperty(value = "是否可显示")
    private Boolean isShow;
}
