package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "UpdatePricedProductCommand", description = "修改产品的命令")
public class UpdatePricedProductCommand {

    @ApiModelProperty(value = "产品代码")
    private String code;

    @ApiModelProperty(value = "产品名称")
    private String name;

    @ApiModelProperty(value = "产品类型（普通产品还是组合产品）")
    private String type;

    @ApiModelProperty(value = "产品分类")
    private String category;

    @ApiModelProperty(value = "最小申报价值")
    private BigDecimal minDeclaredValue;

    @ApiModelProperty(value = "最大申报价值")
    private BigDecimal maxDeclaredValue;

    @ApiModelProperty(value = "挂号逾期天数")
    private Integer overdueDay;

    @ApiModelProperty(value = "支持发货类型")
    private List<String> shipmentTypeSupported;

    @ApiModelProperty(value = "時效最小值（天）")
    private Integer limitationDayMin;

    @ApiModelProperty(value = "時效最大值（天）")
    private Integer limitationDayMax;

    @ApiModelProperty(value = "产品服务")
    private String service;

    @ApiModelProperty(value = "子产品")
    private List<String> subProducts;

    @ApiModelProperty(value = "挂号服务名称")
    private String logisticsRouteId;

    @ApiModelProperty(value = "终端运输商")
    private String terminalCarrier;

    @ApiModelProperty(value = "轨迹官网地址")
    private String trackWebsite;

    @ApiModelProperty(value = "黑名单客户")
    private List<String> blackList;

    @ApiModelProperty(value = "白名单客户")
    private List<String> whiteList;

}
