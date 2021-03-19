package com.szmsd.finance.dto;

import com.szmsd.finance.enums.BillEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 支付实体
 * @author liulei
 */
@Data
public class CustPayDTO {
    @ApiModelProperty(value = "客户id")
    private Long cusId;

    @ApiModelProperty(value = "客户编码")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "支付类型")
    private BillEnum.PayType payType;

    @ApiModelProperty(value = "支付方式")
    private BillEnum.PayMethod payMethod;

    @ApiModelProperty(value = "币种id")
    private Long currencyId;

    @ApiModelProperty(value = "币种编码")
    private String currencyCode;

    @ApiModelProperty(value = "币种名")
    private String currencyName;

    @ApiModelProperty(value = "金额")
    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal amount;

    @ApiModelProperty(value = "比率")
    private BigDecimal rate;

    @ApiModelProperty(value = "币种id2")
    private Long currencyId2;

    @ApiModelProperty(value = "币种名2")
    private String currencyName2;

    @ApiModelProperty(value = "充值渠道")
    private String method;

    @ApiModelProperty(value = "银联银行代码")
    private String bankCode;

    @ApiModelProperty(value = "充值说明")
    private String remark;
}
