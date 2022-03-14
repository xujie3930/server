package com.szmsd.http.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class AnalysisInfoPacking {

    private BigDecimal Length;
    private BigDecimal Width;
    private BigDecimal Height;
    private String Unit;
}
