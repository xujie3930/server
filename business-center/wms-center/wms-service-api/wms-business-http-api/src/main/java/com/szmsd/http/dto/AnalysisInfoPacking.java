package com.szmsd.http.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalysisInfoPacking {

    private BigDecimal Length;
    private BigDecimal Width;
    private BigDecimal Height;
    private String Unit;
}
