package com.szmsd.finance.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BillBusinessTotalVO implements Serializable {

    @ApiModelProperty(value = "性质")
    private String nature;

    @ApiModelProperty(value = "仓库")
    private String warehouse;

    @ApiModelProperty(value = "业务类型")
    private String businessType;

    @ApiModelProperty(value = "费用类别")
    private String chargeCategory;

    @ApiModelProperty(value = "金额")
    private BigDecimal amount;

    @ApiModelProperty(value = "币种")
    private String currencyCode;

    @ApiModelProperty(value = "备注")
    private String remark;
}
