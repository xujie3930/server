package com.szmsd.finance.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ExchangeRateExcelVO implements Serializable {

    @ExcelProperty("原币别")
    private String exchangeFrom;

    @ExcelProperty("现币别")
    private String exchangeTo;

    @ExcelProperty("比率")
    private BigDecimal rate;

    @ExcelProperty("失效时间")
    private String expireTime;

    @ExcelProperty("备注")
    private String remark;

}
