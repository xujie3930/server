package com.szmsd.http.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DirectExpressOrderPack {

    private BigDecimal length;

    private BigDecimal width;

    private BigDecimal height;


}
