package com.szmsd.finance.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExchangeRateExcelVO implements Serializable {

    @ExcelProperty(value = "原币种")
    private String exchangeFrom;

    @ExcelProperty(value = "转换币种")
    private String exchangeTo;

    @ExcelProperty(value = "币种")
    private BigDecimal rate;

    @ExcelProperty(value = "失效时间")
    private String expireTime;

    @ExcelProperty(value = "备注")
    private String remark;

}
