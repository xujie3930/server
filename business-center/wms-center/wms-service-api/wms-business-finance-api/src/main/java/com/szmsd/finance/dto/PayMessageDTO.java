package com.szmsd.finance.dto;

import com.szmsd.finance.enums.PayScoketEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PayMessageDTO implements Serializable {

    @ApiModelProperty(value = "授权")
    private String authorization;

    @ApiModelProperty(value = "")
    private PayScoketEnum payStatus;

    @ApiModelProperty(value = "订单号")
    private String orderNo;
}
