package com.szmsd.finance.vo.helibao;

import com.szmsd.finance.enums.PayEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PayRequestVO implements Serializable {

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付类型")
    private PayEnum payType;
}
