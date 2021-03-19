package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author liulei
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "账户余额表", description = "账户余额表")
@TableName("fss_account_balance")
public class AccountBalance extends FssBaseEntity {
    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "客户id")
    private Long cusId;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "余额")
    private BigDecimal currentBalance;

    public AccountBalance() {
    }

    public AccountBalance(Long cusId, String currencyCode, String currencyName, BigDecimal currentBalance) {
        this.cusId = cusId;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.currentBalance = currentBalance;
    }
}
