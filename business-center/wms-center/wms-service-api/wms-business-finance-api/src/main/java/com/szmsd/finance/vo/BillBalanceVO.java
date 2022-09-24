package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "资金结余")
public class BillBalanceVO implements Serializable {

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "开始时间")
    private String billStartTime;

    @ApiModelProperty(value = "结束时间")
    private String billEndTime;

    @ApiModelProperty(value = "币种明细")
    private List<BasCurrencyVO> basCurrencys;

    @ApiModelProperty("费用类别")
    private List<BillChargeCategoryVO> chargeCategorys;

}
