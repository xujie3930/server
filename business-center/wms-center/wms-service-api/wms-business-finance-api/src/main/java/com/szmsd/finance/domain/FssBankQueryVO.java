package com.szmsd.finance.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class FssBankQueryVO implements Serializable {

    private String bankCode;

    private String currencyCode;
}
