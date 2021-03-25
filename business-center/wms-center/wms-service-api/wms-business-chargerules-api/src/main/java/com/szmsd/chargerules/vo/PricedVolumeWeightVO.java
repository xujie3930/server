package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedVolumeWeightVO", description = "体积重")
public class PricedVolumeWeightVO {

    @ApiModelProperty("类型 - 体积重计费类型")
    private String volumeWeightType;

    @ApiModelProperty("体积重系数 - 体积重系数")
    private Integer volumeWeightStandards;

    @ApiModelProperty("计泡率 - 计泡系数")
    private BigDecimal volumeWeightReduce;

    @ApiModelProperty("最小总物理重量 - 最小重量(物理重)")
    private BigDecimal minPhysicalWeight;

    @ApiModelProperty("最大总物理重量 - 最大重量(物理重)")
    private BigDecimal maxPhysicalWeight;

    @ApiModelProperty("包裹总尺寸（0.00*0.00*0.00）")
    private String packingLimitStr;

    @ApiModelProperty("L+(W+H)*2 - 最大L+(W+H)2 [长+(宽+高)2]")
    private BigDecimal volumeLong;

    @ApiModelProperty("L*W*H - 最大 LWH (体积)")
    private BigDecimal volume;

    @ApiModelProperty("L+W+H - 最大L+W+H（长+宽+高）")
    private BigDecimal perimeter;
}
