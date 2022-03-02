package com.szmsd.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class LogisticsLink {

    private String FromNode;
    private String ToNode;
    private ChargeWrapperOfPricingChargeInfo ChargeModel;
    private List<String> RefNos;
}
