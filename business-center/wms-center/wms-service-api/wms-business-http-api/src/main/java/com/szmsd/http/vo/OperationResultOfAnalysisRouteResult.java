package com.szmsd.http.vo;

import lombok.Data;

@Data
public class OperationResultOfAnalysisRouteResult {

    private AnalysisRouteResult Data;
    private Boolean Succeeded;
    private OperationError Error;
    private String TicketId;
}
