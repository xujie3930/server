package com.szmsd.http.dto.custom;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricesDiscountDetailDto", description = "客户方案-折扣明细")
public class DiscountDetailPackageLimitDto {

    @ApiModelProperty("最小重量（计费重）")
    private BigDecimal minWeight;


    @ApiModelProperty("最大重量（计费重）")
    private BigDecimal maxWeight;

    @ApiModelProperty("最大重量(物理重)")
    private BigDecimal maxPhysicalWeight;


    @ApiModelProperty("最大重量(物理重)")
    private BigDecimal minPhysicalWeight;
    
    @ApiModelProperty("packingLimit")
    private CommonPackingLimit packingLimit;

    @ApiModelProperty("minPackingLimit")
    private CommonPackingLimit minPackingLimit;

    @ApiModelProperty("数量段--最小数量")
    private Integer minQuantity;

    @ApiModelProperty("数量段--最大数量")
    private Integer maxQuantity;

    @ApiModelProperty("不得大于物理重倍数")
    private BigDecimal physicalWeightTimes;

    @ApiModelProperty("最大L+(W+H)2 [长+(宽+高)2]")
    private BigDecimal volumeLong;

    @ApiModelProperty("最大 LWH (体积)")
    private BigDecimal volume;

    @ApiModelProperty("最大L+W+H（长+宽+高）")
    private BigDecimal perimeter;

    @ApiModelProperty("最小 L+(W+H)2 [长+(宽+高)2]")
    private BigDecimal minVolumeLong;

    @ApiModelProperty("最小 LWH (体积)")
    private BigDecimal minVolume;

    @ApiModelProperty("最小 L+W+H（长+宽+高）")
    private BigDecimal minPerimeter;

    @ApiModelProperty("税收国家")
    private String tariffCountry;

    @ApiModelProperty("最小申报价值")
    private BigDecimal minDeclareValue;

    @ApiModelProperty("最大申报价值")
    private BigDecimal maxDeclareValue;

}
