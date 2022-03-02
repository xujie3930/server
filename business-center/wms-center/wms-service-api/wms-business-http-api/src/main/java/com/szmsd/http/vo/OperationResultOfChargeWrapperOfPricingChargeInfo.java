package com.szmsd.http.vo;

import lombok.Data;

@Data
public class OperationResultOfChargeWrapperOfPricingChargeInfo {

    private ChargeWrapperOfPricingChargeInfo Data;
    private Boolean Succeeded;
    private OperationError Error;
    private String TicketId;
}
