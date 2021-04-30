package com.szmsd.http.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddSkuInspectionRequest {

    private String warehouseCode;

    private String refOrderNo;

    private List<String> details;

    public AddSkuInspectionRequest(String warehouseCode, List<String> details) {
        this.warehouseCode = warehouseCode;
        this.details = details;
    }
}
