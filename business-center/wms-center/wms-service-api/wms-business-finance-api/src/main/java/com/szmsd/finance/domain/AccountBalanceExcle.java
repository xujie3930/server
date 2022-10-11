package com.szmsd.finance.domain;


import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;


@Data
public class AccountBalanceExcle {

    @ApiModelProperty(value = "客户编码")
    @Excel(name="客户编码",width = 15)
    private String cusCode;


    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @Excel(name="注册时间",width = 30)
    private String createTimes;



    @ApiModelProperty(value = "币种名")
    @Excel(name="币别",width = 10)
    private String currencyName;

    @ApiModelProperty(value = "总余额")
    @Excel(name="总余额",width = 10)
    private BigDecimal totalBalance;

    @ApiModelProperty(value = "可用余额")
    @Excel(name="可用余额",width = 10)
    private BigDecimal currentBalance;

    @ApiModelProperty(value = "冻结余额")
    @Excel(name="冻结余额",width = 10)
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "授信类型(0：额度，1：类型)")
    @Excel(name="授信类型",width = 10)
    private String creditType;

    @ApiModelProperty(value = "授信结束时间")
    @Excel(name="账期截止时间",width = 30)
    private String creditEndTime;

//    @Excel(name="账期截止时间",width = 30)
//    private String creditEndTimes;



    @ApiModelProperty(value = "授信缓冲截止时间")
    @Excel(name="授信缓冲截止时间",width = 30)
    private String creditBufferTime;

//    @Excel(name="授信缓冲截止时间",width = 30)
//    private String creditBufferTimes;
}
