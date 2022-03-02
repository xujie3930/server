package com.szmsd.http.vo;

import lombok.Data;

@Data
public class ChargeItem {

    private ChargeCategory ChargeCategory;

    private Money Money;

    private String Remark;
}
