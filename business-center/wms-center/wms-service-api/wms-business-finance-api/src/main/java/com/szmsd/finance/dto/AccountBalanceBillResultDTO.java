package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountBalanceBillResultDTO implements Serializable {

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;
}
