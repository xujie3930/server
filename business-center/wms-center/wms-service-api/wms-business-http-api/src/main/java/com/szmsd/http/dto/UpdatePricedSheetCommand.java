package com.szmsd.http.dto;

import com.szmsd.http.vo.PackageLimit;
import com.szmsd.http.vo.PricedVolumeWeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "UpdatePricedSheetCommand", description = "修改报价表详情的命令")
public class UpdatePricedSheetCommand {

    @ApiModelProperty("报价表编号")
    private String code;

    @ApiModelProperty("报价表名称")
    private String name;

    @ApiModelProperty("重量单位")
    private String weightUnit;

    @ApiModelProperty("长度单位")
    private String lengthUnit;

    @ApiModelProperty("货币")
    private String currencyCode;

    @ApiModelProperty("燃油费")
    private BigDecimal fuelTax;

    @ApiModelProperty("基础运费的费用计费方式")
    private String baseChargeCalculateMethod;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("Limit")
    private PackageLimit limit;

    @ApiModelProperty("ItemLimit")
    PackageLimit itemLimit;

    @ApiModelProperty("VolumeWeights")
    private List<PricedVolumeWeight> volumeWeights;

}
