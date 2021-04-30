package com.szmsd.returnex.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@ApiModel(value = "CusWalletVO", description = "我的钱包")
public class CusWalletVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "币种")
    private String currency;

    @ApiModelProperty(value = "可用余额")
    private String currentBalance;

    @ApiModelProperty(value = "冻结余额")
    private String freezeBalance;

    @ApiModelProperty(value = "总余额")
    private String totalBalance;

}
