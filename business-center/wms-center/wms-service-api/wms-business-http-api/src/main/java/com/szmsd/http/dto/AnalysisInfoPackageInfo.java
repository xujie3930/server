package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AnalysisInfoPackageInfo {

    private Weight Weight;

    private AnalysisInfoPacking Packing;

    private Integer Quantity;

    private String RefNo;

    private BigDecimal DeclareValue;
}
