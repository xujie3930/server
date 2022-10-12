package com.szmsd.finance.vo.helibao;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayCallbackParamsVO implements Serializable {

    private String productCode;

    private String orderNo;

    private String merchantNo;

    private String content;

    private String sign;
}
