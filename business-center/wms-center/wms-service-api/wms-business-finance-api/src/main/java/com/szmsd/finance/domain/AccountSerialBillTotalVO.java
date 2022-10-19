package com.szmsd.finance.domain;

import com.szmsd.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountSerialBillTotalVO implements Serializable {

    @Excel(name = "客户代码")
    private String cusCode;

    @Excel(name = "币种")
    private String currencyCode;

    @Excel(name = "币种名称")
    private String currencyName;

    @Excel(name = "发生额")
    private BigDecimal amount;

    @Excel(name = "仓库名称")
    private String warehouseName;

    @Excel(name = "产品代码")
    private String shipmentRule;

    @Excel(name = "产品名称")
    private String shipmentRuleName;

    @Excel(name = "性质")
    private String nature;

    @Excel(name = "费用类型")
    private String chargeCategory;

    @Excel(name = "业务类别")
    private String chargeType;

}
