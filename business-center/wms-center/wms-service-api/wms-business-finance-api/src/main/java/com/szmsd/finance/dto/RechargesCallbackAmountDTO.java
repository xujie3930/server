package com.szmsd.finance.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author liulei
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "RechargesRequestAmountDTO")
public class RechargesCallbackAmountDTO {

    @JsonProperty("Amount")
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @JsonProperty("CurrencyCode")
    @ApiModelProperty(value = "币种编码")
    private String currencyCode;
}
