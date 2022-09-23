package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@ApiModel(value = "生成账单请求参数")
public class BillGeneratorRequestVO implements Serializable {

    @ApiModelProperty(value = "客户代码")
    @NotNull(message = "客户代码不能为空")
    @NotEmpty(message = "客户代码不能为空")
    private String cusCode;

    @ApiModelProperty(value = "开始时间")
    @NotNull(message = "开始时间不能为空")
    @NotEmpty(message = "开始时间不能为空")
    private String billStartTime;

    @ApiModelProperty(value = "结束时间")
    @NotNull(message = "结束时间不能为空")
    @NotEmpty(message = "结束时间不能为空")
    private String billEndTime;
}
