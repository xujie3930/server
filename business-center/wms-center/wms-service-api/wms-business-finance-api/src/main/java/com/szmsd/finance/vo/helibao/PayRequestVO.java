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

    @ApiModelProperty(value = "金额")
    @NotNull(message = "金额不能为空")
    @DecimalMin(value = "0.01",message = "金额必须大于0.01")
    private BigDecimal amount;

    @ApiModelProperty(value = "支付类型")
    @NotNull(message = "支付类型不能为空")
    private PayEnum payType;

    @ApiModelProperty(value = "客户编码")
    @NotNull(message = "客户编码不能为空")
    @NotEmpty(message = "客户编码不能为空")
    private String cusCode;
}
