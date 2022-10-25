package com.szmsd.finance.vo.helibao;

import com.szmsd.finance.enums.PayEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PayRequestVO implements Serializable {

    @ApiModelProperty(value = "充值金额")
    @NotNull(message = "充值金额不能为空")
    @DecimalMin(value = "0.01",message = "金额必须大于0.01")
    private BigDecimal amount;

    @ApiModelProperty(value = "手续费")
    @NotNull(message = "手续费不能为空")
    @DecimalMin(value = "0.01",message = "金额必须大于0.01")
    private BigDecimal procedureAmount;

    @ApiModelProperty(value = "实际支付金额")
    @NotNull(message = "实际支付金额不能为空")
    @DecimalMin(value = "0.01",message = "金额必须大于0.01")
    private BigDecimal acturlAmount;

    @ApiModelProperty(value = "支付类型")
    @NotNull(message = "支付类型不能为空")
    private PayEnum payType;

    @ApiModelProperty(value = "客户编码")
    @NotNull(message = "客户编码不能为空")
    @NotEmpty(message = "客户编码不能为空")
    private String cusCode;
}
