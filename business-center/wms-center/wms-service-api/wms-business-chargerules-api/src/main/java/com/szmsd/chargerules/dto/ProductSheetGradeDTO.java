package com.szmsd.chargerules.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ProductSheetGradeDTO", description = "修改生效时间")
public class ProductSheetGradeDTO {

    @ApiModelProperty(value = "产品代码")
    private String productCode;

    @ApiModelProperty(value = "报价表编号")
    private String sheetCode;

    @ApiModelProperty(value = "等级")
    private String grade;

    @ApiModelProperty("生效开始时间")
    private String effectiveStartTime;

    @ApiModelProperty("生效结束时间")
    private String effectiveEndTime;

}
