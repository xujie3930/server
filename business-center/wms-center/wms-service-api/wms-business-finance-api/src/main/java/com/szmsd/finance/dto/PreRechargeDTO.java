package com.szmsd.finance.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liulei
 */
@Data
public class PreRechargeDTO {
    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "客户id")
    private Long cusId;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "请求参数唯一标识/流水号")
    private String serialNo;

    @ApiModelProperty(value = "汇款方式 0电汇 1转账 2支票")
    private String remittanceMethod;

    @ApiModelProperty(value = "币种id")
    private String currencyCode;

    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "汇款日期")
    private Date remittanceTime;

    @ApiModelProperty(value = "收款账号")
    private String gatheringAccount;

    @ApiModelProperty(value = "汇款凭证")
    private String paymentVoucher;

    @ApiModelProperty(value = "审核状态(默认0=未审核，1=审核，2=审核未通过)")
    private String verifyStatus;

    @ApiModelProperty(value = "审核备注")
    private String verifyRemark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "审核日期")
    private Date verifyDate;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "开始时间")
    private String beginTime;

    @ApiModelProperty(value = "结束时间")
    private String endTime;
}
