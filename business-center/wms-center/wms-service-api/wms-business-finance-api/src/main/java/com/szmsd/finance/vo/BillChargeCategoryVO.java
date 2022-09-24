package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BillChargeCategoryVO implements Serializable {

    @ApiModelProperty(value = "客户代码")
    private String cusCode;

    @ApiModelProperty(value = "名称")
    private String chargeCategory;

    @ApiModelProperty("币种金额")
    private List<BillCurrencyAmountVO> billCurrencyAmounts;
}
