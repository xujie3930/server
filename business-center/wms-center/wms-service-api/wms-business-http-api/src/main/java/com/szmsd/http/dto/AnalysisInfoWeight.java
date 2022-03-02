package com.szmsd.http.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AnalysisInfoWeight {

    private BigDecimal Value;
    private String Unit;
}
