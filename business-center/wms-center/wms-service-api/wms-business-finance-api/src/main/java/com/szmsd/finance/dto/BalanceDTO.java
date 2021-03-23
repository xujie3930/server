package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author liulei
 */
@Data
public class BalanceDTO {
    @ApiModelProperty(value = "可用余额")
    private BigDecimal currentBalance;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "总余额")
    private BigDecimal totalBalance;

    public BalanceDTO() {}

    public BalanceDTO(BigDecimal currentBalance, BigDecimal freezeBalance, BigDecimal totalBalance) {
        this.currentBalance = currentBalance;
        this.freezeBalance = freezeBalance;
        this.totalBalance = totalBalance;
    }
}
