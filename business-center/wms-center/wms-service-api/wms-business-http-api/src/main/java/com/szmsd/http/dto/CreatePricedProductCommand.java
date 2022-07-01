package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CreatePricedProductCommand", description = "创建产品的命令")
public class CreatePricedProductCommand  {

    @ApiModelProperty(value = "产品代码")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "产品类型（普通产品还是组合产品）")
    private String type;

    @ApiModelProperty(value = "产品分类")
    private String category;

    @Deprecated
    @ApiModelProperty(value = "产品服务", hidden = true)
    private String service;

    @ApiModelProperty(value = "支持发货类型")
    private List<String> shipmentTypeSupported;

    @ApiModelProperty(value = "挂号逾期天数")
    private Integer overdueDay;

    @Deprecated
    @ApiModelProperty(value = "子产品", hidden = true)
    private List<String> subProducts;

    @ApiModelProperty(value = "挂号服务名称")
    private String logisticsRouteId;

    @ApiModelProperty(value = "终端运输商")
    private String terminalCarrier;

    @Deprecated
    @ApiModelProperty(value = "轨迹官网地址")
    private String trackWebsite;



}
