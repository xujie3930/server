package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "生成账单请求参数")
public class GeneratorBillRequestVO implements Serializable {

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "开始时间")
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
