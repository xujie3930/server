package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class AccountSyncErrorEmailDTO implements Serializable {

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "客户钱包昨日金额")
    private BigDecimal yestodayAmount;

    @ApiModelProperty(value = "客户钱包今日金额")
    private BigDecimal todayAmount;

    @ApiModelProperty(value = "客户今日发生金额")
    private BigDecimal changeAmount;
}
