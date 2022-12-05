package com.szmsd.finance.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class RefundRequestListAutoDTO {

    @ApiModelProperty(value = "退款申请")
    private List<RefundRequestAutoDTO> refundRequestList;
}
