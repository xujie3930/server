package com.szmsd.finance.domain;

import com.szmsd.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class AccountSerialBillTotalVO implements Serializable {


    @Excel(name = "结算时间")
    private String paymentTime;

    @Excel(name = "客户经理")
    private String serviceManagerNickName;

    @Excel(name = "客服")
    private String serviceStaffNickName;

    @Excel(name = "客户代码")
    private String cusCode;

    @Excel(name = "仓库名称")
    private String warehouseName;

    @Excel(name = "性质")
    private String businessCategory;

    @Excel(name = "业务类型")
    private String chargeCategory;

    @Excel(name = "产品名称")
    private String productCode;

    @Excel(name = "金额")
    private BigDecimal amount;

    @Excel(name = "币种")
    private String currencyCode;


}
