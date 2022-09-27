package com.szmsd.finance.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BillBalanceExcelResultVO implements Serializable {


    private String chargeCategory;

    private BigDecimal cny;

    private BigDecimal gbp;

    private BigDecimal usd;

    private BigDecimal aud;

    private BigDecimal eur;

    private BigDecimal cad;

    private BigDecimal hkd;

    private BigDecimal jpy;

}
