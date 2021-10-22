package com.szmsd.finance.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

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

    @ApiModelProperty(value = "可用余额")
    private BigDecimal currentBalance;

    @ApiModelProperty(value = "冻结余额")
    private BigDecimal freezeBalance;

    @ApiModelProperty(value = "总余额")
    private BigDecimal totalBalance;
    /** 09-07 授信额度新增 */
    @ApiModelProperty(value = "授信类型(0：额度，1：类型)")
    @Excel(name = "授信类型(0：额度，1：类型)")
    private String creditType;

    @ApiModelProperty(value = "授信状态（0：未启用，1：启用中，2：欠费停用，3：已禁用）")
    @Excel(name = "授信状态（0：未启用，1：启用中，2：欠费停用，3：已禁用）")
    private Integer creditStatus;

    @ApiModelProperty(value = "授信额度")
    @Excel(name = "授信额度")
    private BigDecimal creditLine;

    @ApiModelProperty(value = "使用额度金额")
    @Excel(name = "使用额度金额")
    private BigDecimal creditUseAmount;

    @ApiModelProperty(value = "授信开始时间")
    @Excel(name = "授信开始时间")
    private LocalDateTime creditBeginTime;

    @ApiModelProperty(value = "授信结束时间")
    @Excel(name = "授信结束时间")
    private LocalDateTime creditEndTime;

    @ApiModelProperty(value = "授信时间间隔")
    @Excel(name = "授信时间间隔")
    private Integer creditTimeInterval;

    @ApiModelProperty(value = "授信时间单位")
    @Excel(name = "授信时间单位")
    private String creditTimeUnit;

    @ApiModelProperty(value = "授信缓冲截止时间")
    @Excel(name = "授信缓冲截止时间")
    private LocalDateTime creditBufferTime;

    @ApiModelProperty(value = "授信缓冲时间间隔")
    @Excel(name = "授信缓冲时间间隔")
    private Integer creditBufferTimeInterval;

    @ApiModelProperty(value = "授信缓冲时间单位")
    @Excel(name = "授信缓冲时间单位")
    private String creditBufferTimeUnit;

    @ApiModelProperty(value = "缓冲时间使用额度")
    @Excel(name = "缓冲时间使用额度")
    private BigDecimal creditBufferUseAmount;

    public AccountBalance() {}

    public AccountBalance(String cusCode, String currencyCode, String currencyName) {
        this.cusCode = cusCode;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
        this.currentBalance = BigDecimal.ZERO;
        this.freezeBalance = BigDecimal.ZERO;
        this.totalBalance = BigDecimal.ZERO;
        this.creditUseAmount = BigDecimal.ZERO;
        this.creditBufferUseAmount = BigDecimal.ZERO;
    }

    public void showCredit() {
        this.creditUseAmount = Optional.ofNullable(this.creditUseAmount).orElse(BigDecimal.ZERO);
        this.creditBufferUseAmount = Optional.ofNullable(this.creditBufferUseAmount).orElse(BigDecimal.ZERO);
        this.currentBalance = this.currentBalance.subtract(this.creditUseAmount).subtract(this.creditBufferUseAmount);
        this.totalBalance = this.totalBalance.subtract(this.creditUseAmount).subtract(this.creditBufferUseAmount);
    }
}
