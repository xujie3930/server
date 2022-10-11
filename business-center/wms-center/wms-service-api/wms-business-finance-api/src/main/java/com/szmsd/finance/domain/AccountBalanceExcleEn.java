package com.szmsd.finance.domain;


import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class AccountBalanceExcleEn {

    @ApiModelProperty(value = "客户编码")
    @Excel(name="cusCode",width = 15)
    private String cusCode;


    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Excel(name="createTime",width = 30)
    private String createTimes;



    @ApiModelProperty(value = "币种名")
    @Excel(name="currencyName",width = 10)
    private String currencyName;

    @ApiModelProperty(value = "总余额")
    @Excel(name="totalBalance",width = 10)
    private BigDecimal totalBalance;

    @ApiModelProperty(value = "可用余额")
    @Excel(name="currentBalance",width = 10)
    private BigDecimal currentBalance;

    @ApiModelProperty(value = "冻结余额")
    @Excel(name="freezeBalance",width = 10)
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "授信类型(0：额度，1：类型)")
    @Excel(name="creditType",width = 10)
    private String creditType;

    @ApiModelProperty(value = "授信结束时间")
    private Date creditEndTime;

    @Excel(name="creditEndTime",width = 30)
    private String creditEndTimes;



    @ApiModelProperty(value = "授信缓冲截止时间")
    private Date creditBufferTime;

    @Excel(name="creditBufferTime",width = 30)
    private String creditBufferTimes;
}
