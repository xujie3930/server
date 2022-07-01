package com.szmsd.http.dto.custom;

import com.szmsd.http.vo.Operation;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "CustomPricesMainDto", description = "客户方案主类")
public class CustomPricesMainDto {

    @ApiModelProperty("客户编号")
    private String clientCode;

    @ApiModelProperty("客户等级方案")
    private List<CustomPricesGradeDto> customGradeTemplates;

    @ApiModelProperty("客户折扣方案")
    private List<CustomPricesDiscountDto> customDiscountTemplates;
}
