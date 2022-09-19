package com.szmsd.finance.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountBalanceUpdateDTO implements Serializable {

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "币种id")
    private String currencyCode;

    @ApiModelProperty(value = "可用余额")
    private BigDecimal currentBalance;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "总余额")
    private BigDecimal totalBalance;

    @ApiModelProperty(value = "使用额度金额")
    private BigDecimal creditUseAmount;

    @ApiModelProperty(value = "授信开始时间")
    private LocalDateTime creditBeginTime;

    @ApiModelProperty(value = "授信结束时间")
    private LocalDateTime creditEndTime;

    @ApiModelProperty(value = "需要偿还金额")
    private BigDecimal creditRepaidAmount;

    @ApiModelProperty(value = "授信时间间隔")
    private Integer creditTimeInterval;

    @ApiModelProperty(value = "授信时间单位")
    private String creditTimeUnit;

    @ApiModelProperty(value = "授信缓冲截止时间")
    private LocalDateTime creditBufferTime;

    @ApiModelProperty(value = "授信缓冲时间间隔")
    private Integer creditBufferTimeInterval;

    @ApiModelProperty(value = "授信缓冲时间单位")
    private String creditBufferTimeUnit;

    @ApiModelProperty(value = "缓冲时间使用额度")
    private Boolean creditTimeFlag;

    @ApiModelProperty(value = "授信状态（0：未启用，1：启用中，2：欠费停用，3：已禁用）")
    private Integer creditStatus;

    private Long version;
}
