package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(value = "ChangeSheetGradeCommand", description = "修改一个物流产品信息的报价表对应的等级")
public class ChangeSheetGradeCommand {

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
