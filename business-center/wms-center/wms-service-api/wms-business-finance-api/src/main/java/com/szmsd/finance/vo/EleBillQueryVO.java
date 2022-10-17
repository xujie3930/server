package com.szmsd.finance.vo;

import com.szmsd.finance.BaseQueryVO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value = "查询条件")
public class EleBillQueryVO extends BaseQueryVO {

    @ApiModelProperty("客户代码")
    private String cusCode;

    @ApiModelProperty(value = "账单开始时间")
    @NotNull(message = "开始时间不能为空")
    @NotEmpty(message = "开始时间不能为空")
    private String billStartTime;

    @ApiModelProperty(value = "账单结束时间")
    @NotNull(message = "结束时间不能为空")
    @NotEmpty(message = "结束时间不能为空")
    private String billEndTime;

    @ApiModelProperty(value = "订单类型")
    private List<String> orderTypeList;

    @ApiModelProperty(value = "sheet 页数")
    private Integer sheetNo;

    @ApiModelProperty(value = "客户代码集合")
    private List<String> cusCodes;


}
