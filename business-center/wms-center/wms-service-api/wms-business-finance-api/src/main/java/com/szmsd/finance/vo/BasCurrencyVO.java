package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class BasCurrencyVO implements Serializable {

    @ApiModelProperty(value = "币种符号")
    private String currencyCode;

    @ApiModelProperty(value = "币种名称")
    private String currencyName;
}
