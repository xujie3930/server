package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountBalanceBillCurrencyVO implements Serializable {

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
}
