package com.szmsd.http.dto.recharges;

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
public class RechargesRequestAmountDTO {
    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;
}
