package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author liulei
 */
@Data
public class CusFreezeBalanceDTO {
    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "单号")
    private String no;

    @ApiModelProperty(value = "操作类型")
    private String orderType;

    @ApiModelProperty(value = "金额")
    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    public CusFreezeBalanceDTO() {

    }

    public CusFreezeBalanceDTO(String cusCode, String currencyCode, String no, String orderType, BigDecimal amount) {
        this.cusCode = cusCode;
        this.currencyCode = currencyCode;
        this.no = no;
        this.orderType = orderType;
        this.amount = amount;
    }

}
