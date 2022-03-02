package com.szmsd.http.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Money {

    private BigDecimal Amount;

    private String CurrencyCode;
}
