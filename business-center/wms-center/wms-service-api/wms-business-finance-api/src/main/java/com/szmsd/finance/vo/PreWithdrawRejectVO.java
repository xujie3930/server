package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class PreWithdrawRejectVO {

    @ApiModelProperty(value = "IDs")
    private List<String> ids;

    @ApiModelProperty(value = "退回原因")
    @Size(max = 200,message = "退回原因大小不允许超过200个字符")
    private String rejectRemark;

}
