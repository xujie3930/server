package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ApiModel(description = "资金结余")
public class BillBalanceExcelResultVO implements Serializable {

    @ApiModelProperty("客户代码")
    private String cusCode;

    @ApiModelProperty("账期起止时间")
    private String resultTime;

    @ApiModelProperty("费用类别")
    private String chargeCategory;

    @ApiModelProperty("人民币")
    private BigDecimal cny;

    @ApiModelProperty("英镑")
    private BigDecimal gbp;

    @ApiModelProperty("美元")
    private BigDecimal usd;

    @ApiModelProperty("")
    private BigDecimal aud;

    @ApiModelProperty("欧元")
    private BigDecimal eur;

    private BigDecimal cad;

    @ApiModelProperty("港币")
    private BigDecimal hkd;

    @ApiModelProperty("日元")
    private BigDecimal jpy;

}
