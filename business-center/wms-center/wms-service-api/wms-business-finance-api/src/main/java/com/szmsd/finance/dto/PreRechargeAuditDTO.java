package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liulei
 */
@Data
public class PreRechargeAuditDTO {

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "审核状态(默认0=未审核，1=审核通过，2=审核未通过)")
    private String verifyStatus;

    @ApiModelProperty(value = "审核备注")
    private String verifyRemark;
}
