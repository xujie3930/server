package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "PricedSheetCodeCriteria", description = "模板查询器")
public class PricedSheetCodeCriteria {

    @ApiModelProperty(value = "模板Code")
    private String sheetCode;

}
