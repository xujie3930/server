package com.szmsd.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class ChargeWrapperOfPricingChargeInfo {

    private PricingChargeInfo Data;
    private ChargeCategory ServiceCategory;
    private List<ChargeItem> Charges;
}
