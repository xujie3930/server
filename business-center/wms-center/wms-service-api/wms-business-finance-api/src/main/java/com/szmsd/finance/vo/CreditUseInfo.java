package com.szmsd.finance.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @ClassName: CreditUseInfo
 * @Description:
 * @Author: 11
 * @Date: 2021-10-26 9:40
 */
@Data
public class CreditUseInfo {
    private String currencyCode;
    private BigDecimal creditUseAmount;
}
