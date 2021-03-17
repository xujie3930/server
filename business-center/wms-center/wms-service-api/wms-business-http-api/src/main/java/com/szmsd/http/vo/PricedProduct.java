package com.szmsd.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricedProduct {

    @ApiModelProperty(value = "产品代码")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "服务商名称 - 挂号服务")
    private String logisticsRouteId;

    @ApiModelProperty(value = "是否可下单")
    private Boolean inService;

    @ApiModelProperty(value = "是否可显示")
    private Boolean isShow;

}
