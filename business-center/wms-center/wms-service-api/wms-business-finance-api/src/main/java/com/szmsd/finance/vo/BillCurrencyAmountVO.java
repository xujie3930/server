package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BillCurrencyAmountVO implements Serializable {

    @ApiModelProperty(value = "币种代码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
}
