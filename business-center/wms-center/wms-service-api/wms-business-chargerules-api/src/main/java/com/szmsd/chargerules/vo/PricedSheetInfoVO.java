package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedSheetInfoVO", description = "产品报价详情")
public class PricedSheetInfoVO {

    @ApiModelProperty(value = "报价表编号 - 产品代码")
    private String code;

    @ApiModelProperty(value = "报价表名称 - 产品名称")
    private String name;

    @ApiModelProperty("货币单位 - 货币单位")
    private String currencyCode;

    @ApiModelProperty("重量单位 - 重量单位")
    private String weightUnit;

    @ApiModelProperty("尺寸单位 - 尺寸长度单位")
    private String lengthUnit;

    @ApiModelProperty("燃油费 - 燃油费率")
    private BigDecimal fuelTax;

    @ApiModelProperty("运费计算方式 - 基础运费的费用计费方式")
    private String baseChargeCalculateMethod;

    @ApiModelProperty("备注 - 备注信息")
    private String remark;

    @ApiModelProperty("体积重 - 体积重配置")
    private List<PricedVolumeWeightVO> volumeWeights;

    @ApiModelProperty(value = "limit包裹限制条件")
    private PackageLimitVO limit;


}
