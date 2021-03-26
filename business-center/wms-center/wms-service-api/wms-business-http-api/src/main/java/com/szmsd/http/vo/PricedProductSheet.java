package com.szmsd.http.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PricedProductSheet {

    @ApiModelProperty("报价表Id")
    private String sheetId;

    @ApiModelProperty("报价表编号")
    private String code;

    @ApiModelProperty("报价表名称")
    private String name;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("等级")
    private String grade;

    @ApiModelProperty("生效开始时间")
    private String effectiveStartTime;

    @ApiModelProperty("生效结束时间")
    private String effectiveEndTime;

}
