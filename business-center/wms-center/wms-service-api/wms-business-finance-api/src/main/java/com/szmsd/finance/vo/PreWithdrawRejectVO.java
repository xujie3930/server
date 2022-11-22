package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PreWithdrawRejectVO {

    @ApiModelProperty(value = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @ApiModelProperty(value = "退回原因")
    @Size(max = 200,message = "退回原因大小不允许超过200个字符")
    private String rejectRemark;

}
