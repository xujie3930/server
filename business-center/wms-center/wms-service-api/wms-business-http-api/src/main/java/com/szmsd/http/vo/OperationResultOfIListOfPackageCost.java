package com.szmsd.http.vo;

import lombok.Data;

import java.util.List;

@Data
public class OperationResultOfIListOfPackageCost {

    private List<PackageCost> Data;
    private Boolean Succeeded;
    private OperationError Error;
    private String TicketId;
}
