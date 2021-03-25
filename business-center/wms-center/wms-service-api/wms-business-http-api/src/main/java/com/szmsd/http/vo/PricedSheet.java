package com.szmsd.http.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedSheet", description = "报价表详情")
public class PricedSheet {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("报价表代码")
    private String code;

    @ApiModelProperty("所属产品Code")
    private String productCode;

    @ApiModelProperty("报价表名称")
    private String name;

    @ApiModelProperty("备注信息")
    private String remark;

    @ApiModelProperty("等级")
    private String grade;

    @ApiModelProperty(value = "limit包裹限制条件")
    private PackageLimit limit;

    @ApiModelProperty("itemLimit包裹限制条件")
    private PackageLimit itemLimit;

    @ApiModelProperty("货币单位")
    private String currencyCode;

    @ApiModelProperty("重量单位")
    private String weightUnit;

    @ApiModelProperty("尺寸长度单位")
    private String lengthUnit;

    @ApiModelProperty("燃油费率")
    private BigDecimal fuelTax;

    @ApiModelProperty("基础运费的费用计费方式")
    private String baseChargeCalculateMethod;

    @ApiModelProperty("Creation")
    Operation creation;

    @ApiModelProperty("LastModifyOperation")
    Operation lastModifyOperation;

    @ApiModelProperty("报价表分区信息")
    private List<PricedZone> zones;

    @ApiModelProperty("报价表报价信息")
    private List<PricedCharge> charges;

    @ApiModelProperty("报价表附加费信息")
    private List<PricedSurcharge> surcharges;

    @ApiModelProperty("体积重配置")
    private List<PricedVolumeWeight> volumeWeights;

    @ApiModelProperty("费用免收配置")
    private List<PricedExemption> pricingExemptions;

    @ApiModelProperty("地址限制")
    private List<PricedAddressFilter> addressFilter;

    @ApiModelProperty("生效开始时间")
    private String effectiveStartTime;

    @ApiModelProperty("生效结束时间")
    private String effectiveEndTime;

}
