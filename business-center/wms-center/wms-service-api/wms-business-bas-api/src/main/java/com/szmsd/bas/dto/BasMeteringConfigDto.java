package com.szmsd.bas.dto;

import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "BasMeteringConfigDto", description = "计泡查询字段")
public class BasMeteringConfigDto  extends QueryDto {

    @ApiModelProperty(value = "产品code")
    private String logisticsErvicesCode;

    @ApiModelProperty(value = "产品名称")
    private String logisticsErvicesName;

    @ApiModelProperty(value = "国家code")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "客户code")
    private String customerCode;

    @ApiModelProperty(value = "计费重")
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "下单重 g")
    private BigDecimal weight;

    @ApiModelProperty(value = "体积重 g")
    private BigDecimal volume;

}
