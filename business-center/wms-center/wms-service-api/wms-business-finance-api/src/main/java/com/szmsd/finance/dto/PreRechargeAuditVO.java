package com.szmsd.finance.dto;

import com.szmsd.finance.enums.PreRechargeVerifyStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PreRechargeAuditVO {

    @ApiModelProperty(value = "ID")
    @NotNull(message = "主键ID不允许为空")
    private Long id;

    @ApiModelProperty(value = "审核状态(默认UNAPPROVED=未审核，REVIEWED=审核通过，FAILED=审核未通过 ,REJECT=充值驳回)")
    @NotNull(message = "审核状态不允许为空")
    private PreRechargeVerifyStatusEnum verifyStatus;

    @ApiModelProperty(value = "驳回意见")
    private String rejectRemark;
}
