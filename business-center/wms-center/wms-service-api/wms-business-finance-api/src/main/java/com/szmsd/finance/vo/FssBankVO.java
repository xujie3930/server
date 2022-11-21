package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class FssBankVO implements Serializable {

    @ApiModelProperty(value = "ID")
    private String bankId;

    @ApiModelProperty(value = "银行编码")
    private String bankCode;

    @ApiModelProperty(value = "银行名称")
    private String bankName;

    @ApiModelProperty(value = "账号")
    private String bankAccount;

    @ApiModelProperty(value = "币种")
    private String currencyCode;
}
