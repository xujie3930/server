package com.szmsd.http.vo;

import lombok.Data;

@Data
public class ChargeCategory {

    private String BillingNo;

    private String ChargeNameCN;

    private String ChargeNameEN;

    private String ParentBillingNo;
}
