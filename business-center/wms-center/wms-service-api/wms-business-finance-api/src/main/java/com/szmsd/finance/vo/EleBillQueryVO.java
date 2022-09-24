package com.szmsd.finance.vo;

import com.szmsd.finance.BaseQueryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "查询条件")
public class EleBillQueryVO extends BaseQueryVO {

    @ApiModelProperty("客户代码")
    private String cusCode;

    @ApiModelProperty(value = "账单开始时间")
    private String startTime;

    @ApiModelProperty(value = "账单结束时间")
    private String entTime;
}
