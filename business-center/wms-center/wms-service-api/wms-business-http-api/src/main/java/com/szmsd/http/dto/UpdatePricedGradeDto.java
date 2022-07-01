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
@ApiModel(value = "UpdatePricedGradeDto", description = "修改报价等级")
public class UpdatePricedGradeDto {

    @ApiModelProperty("产品编号")
    private String productCode;

    @ApiModelProperty("报价表编号")
    private String sheetCode;

    @ApiModelProperty(value = "等级")
    private String grade;

}
