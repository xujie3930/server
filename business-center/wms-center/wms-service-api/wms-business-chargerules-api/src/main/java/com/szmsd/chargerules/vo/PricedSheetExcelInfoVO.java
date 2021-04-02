package com.szmsd.chargerules.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedSheetExcelInfoVO", description = "报价表信息")
public class PricedSheetExcelInfoVO {

    @ApiModelProperty(value = "报价表编号")
    private String sheetCode;

    @ApiModelProperty("报价表分区信息")
    private List<PricedZoneVO> zones;

    @ApiModelProperty("报价表报价信息")
    private List<PricedChargeVO> charges;

    @ApiModelProperty("报价表附加费信息")
    private List<PricedSurchargeVO> surcharges;

    @ApiModelProperty("地址限制")
    private List<PricedAddressFilterVO> addressFilter;

}
