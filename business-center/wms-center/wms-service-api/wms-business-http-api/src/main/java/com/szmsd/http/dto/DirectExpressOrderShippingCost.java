package com.szmsd.http.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class DirectExpressOrderShippingCost implements Serializable {

    private BigDecimal money;

    private String currency;

    private String type;

    private String remark;

}
