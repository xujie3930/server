package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountBalanceBillResultDTO implements Serializable {

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @ApiModelProperty(value = "上期余额")
    private BigDecimal periodAmount;

    @ApiModelProperty(value = "本期收入")
    private BigDecimal currentIncomeAmount;

    @ApiModelProperty(value = "本期支出")
    private BigDecimal currentExpenditureAmount;

    @ApiModelProperty(value = "本期余额")
    private BigDecimal currentAmount;

    @ApiModelProperty(value = "本期需支付")
    private BigDecimal currentNeedPay;

}
