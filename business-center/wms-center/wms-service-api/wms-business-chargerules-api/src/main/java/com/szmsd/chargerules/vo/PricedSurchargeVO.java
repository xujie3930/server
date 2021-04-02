package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedSurchargeVO", description = "附加费")
public class PricedSurchargeVO {

    @ApiModelProperty("附加费类型 - 费用类型")
    private String chargeType;

    @ApiModelProperty("低于最小边长范围")
    private String minPackingLimitStr;

    @ApiModelProperty("超出最大边长范围")
    private String packingLimitStr;

    @ApiModelProperty("最小计费重量 - 最小重量（计费重）")
    private BigDecimal minWeight;

    @ApiModelProperty("最大计费重量 - 最大重量（计费重）")
    private BigDecimal maxWeight;

    @ApiModelProperty("最小物理重量 - 最小重量(物理重)")
    private BigDecimal minPhysicalWeight;

    @ApiModelProperty("最大计费重量 - 最大重量(物理重)")
    private BigDecimal maxPhysicalWeight;

    @ApiModelProperty("最小数量 - 数量段--最小数量")
    private Integer minQuantity;

    @ApiModelProperty("最大数量 - 数量段--最大数量")
    private Integer maxQuantity;

    @ApiModelProperty("L+(W+H)*2 - 最大L+(W+H)2 [长+(宽+高)2]")
    private BigDecimal volumeLong;

    @ApiModelProperty("L*W*H - 最大 LWH (体积)")
    private BigDecimal volume;

    @ApiModelProperty("L+W+H - 最大L+W+H（长+宽+高）")
    private BigDecimal perimeter;

    @ApiModelProperty("偏远地区模板 - 偏远地区附加费模板")
    private String remoteModel;

    @ApiModelProperty("分区名称 - 分区")
    private String zoneName;

    @ApiModelProperty("Tags - Tag标签")
    private String tags;

    @ApiModelProperty("货币")
    private String currency;

    @ApiModelProperty("费用计费方式")
    private String chargeCalculateMethod;

    @ApiModelProperty("是否用于计算燃油附加费")
    private Boolean isCalcForFuelTax;

    @ApiModelProperty("起始价格 - 起始价格/首重")
    private BigDecimal startPrice;

    @ApiModelProperty("续重重量 - 续费重量")
    private BigDecimal deltaWeightPerStage;

    @ApiModelProperty("续重价格 - 续费价格")
    private BigDecimal deltaChargePerStage;

    @ApiModelProperty("续数数量")
    private Integer detalNumberPerQuantity;

    @ApiModelProperty("续数价格")
    private BigDecimal detalChargePerQuantity;

    @ApiModelProperty("最低价格 - 最小价格")
    private BigDecimal minPrice;

    @ApiModelProperty("最高价格")
    private BigDecimal maxPrice;

    @ApiModelProperty("折扣")
    private BigDecimal percentage;

    @ApiModelProperty("报价类型 - 计价类型")
    private String chargeRuleType;

}
