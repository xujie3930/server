package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PackageLimitVO", description = "包裹总重量尺寸限制")
public class PackageLimitVO {

    @ApiModelProperty("最小计费重量 - 最小重量（计费重）")
    private BigDecimal minWeight;

    @ApiModelProperty("最大计费重量 - 最大重量（计费重）")
    private BigDecimal maxWeight;

    @ApiModelProperty("最小物理重量 - 最小重量(物理重)")
    private BigDecimal minPhysicalWeight;

    @ApiModelProperty("最大物理重量 - 最大重量(物理重)")
    private BigDecimal maxPhysicalWeight;

    @ApiModelProperty("最小尺寸(0.00*0.00*0.00)")
    private String minPackingLimitStr;

    @ApiModelProperty("最大尺寸(0.00*0.00*0.00)")
    private String packingLimitStr;

    @ApiModelProperty("最小L*W*H - 最小 LWH (体积)")
    private BigDecimal minVolume;

    @ApiModelProperty("最大L*W*H - 最大 LWH (体积)")
    private BigDecimal volume;

    @ApiModelProperty("最小L+W+H - 最小 L+W+H（长+宽+高）")
    private BigDecimal minPerimeter;

    @ApiModelProperty("最大L+W+H - 最大L+W+H（长+宽+高）")
    private BigDecimal perimeter;

    @ApiModelProperty("不大于物理重倍数 - 不得大于物理重倍数")
    private BigDecimal physicalWeightTimes;

}
