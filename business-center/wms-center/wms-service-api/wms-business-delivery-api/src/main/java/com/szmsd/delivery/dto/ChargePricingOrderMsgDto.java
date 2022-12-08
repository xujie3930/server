package com.szmsd.delivery.dto;

import lombok.Data;

@Data
public class ChargePricingOrderMsgDto {

    private String orderNo;

    private String state;

    private String errorMsg;
}
