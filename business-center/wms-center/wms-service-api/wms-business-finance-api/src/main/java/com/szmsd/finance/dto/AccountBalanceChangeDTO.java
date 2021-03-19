package com.szmsd.finance.dto;

import com.szmsd.finance.enums.BillEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author liulei
 */
@Data
public class AccountBalanceChangeDTO implements Serializable {
    @ApiModelProperty(value = "客户id")
    private Long cusId;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "流水号")
    private String serialNum;

    @ApiModelProperty(value = "交易类型")
    private BillEnum.PayMethod payMethod;

    @ApiModelProperty(value = "币种id")
    private Long currencyId;

    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "余额变动")
    private BigDecimal amountChange;

    @ApiModelProperty(value = "当前余额")
    private BigDecimal currentBalance;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
