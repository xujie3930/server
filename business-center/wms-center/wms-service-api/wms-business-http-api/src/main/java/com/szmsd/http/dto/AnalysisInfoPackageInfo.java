package com.szmsd.http.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalysisInfoPackageInfo {

    private Weight Weight;

    private AnalysisInfoPacking Packing;

    private Integer Quantity;

    private String RefNo;

    private BigDecimal DeclareValue;
}
