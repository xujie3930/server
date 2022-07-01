package com.szmsd.delivery.dto;

import com.szmsd.http.dto.custom.CommonPackingLimit;
import com.szmsd.http.dto.custom.DiscountDetailFormulaDto;
import com.szmsd.http.dto.custom.DiscountDetailPackageLimitDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomPricesDiscountImportDto", description = "客户方案-导入折扣方案")
public class CustomPricesDiscountImportDto {

    @ApiModelProperty("产品代码")
    private String productCode;

    @ApiModelProperty("费用类型")
    private String chargeType;

    @ApiModelProperty("处理点")
    private String warehourse;


    @ApiModelProperty("低于最小边长范围")
    private String minPackingLimitStr;

    @ApiModelProperty("超出最大边长范围")
    private String packingLimitStr;

    @ApiModelProperty("最小计费重量")
    private BigDecimal minWeight;

    @ApiModelProperty("最大计费重量")
    private BigDecimal maxWeight;

    @ApiModelProperty("最小物理重量")
    private BigDecimal minPhysicalWeight;

    @ApiModelProperty("最大物理重量")
    private BigDecimal maxPhysicalWeight;


    @ApiModelProperty("重量单位")
    private String weightUnit;


    @ApiModelProperty("最小L+(W+H)*2")
    private BigDecimal minVolumeLong;


    @ApiModelProperty("最大L+(W+H)*2")
    private BigDecimal volumeLong;


    @ApiModelProperty("L*W*H")
    private BigDecimal minVolume;

    @ApiModelProperty("L+W+H")
    private BigDecimal minPerimeter;

    @ApiModelProperty("偏远地区模板")
    private String remoteModel;

    @ApiModelProperty("分区名称")
    private String zoneName;

    @ApiModelProperty("起始价格")
    private BigDecimal startPrice;

    @ApiModelProperty("续重重量")
    private BigDecimal deltaWeightPerStage;

    @ApiModelProperty("续重价格")
    private BigDecimal deltaChargePerStage;

    @ApiModelProperty("续数数量")
    private Integer detalNumberPerQuantity;

    @ApiModelProperty("续数价格")
    private BigDecimal detalChargePerQuantity;

    @ApiModelProperty("最低价格")
    private BigDecimal minPrice;

    @ApiModelProperty("最高价格")
    private BigDecimal maxPrice;

    @ApiModelProperty("计费方式")
    private String chargeRuleType;

    @ApiModelProperty("折扣")
    private BigDecimal percentage;

    @ApiModelProperty("生效时间")
    private String beginTime;

    @ApiModelProperty("截止时间")
    private String endTime;

    @ApiModelProperty("销售价格级别")
    private String null1;






















}
