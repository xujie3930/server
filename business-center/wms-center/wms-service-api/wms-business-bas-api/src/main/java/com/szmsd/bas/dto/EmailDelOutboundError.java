package com.szmsd.bas.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "", description = "EmailDelOutboundError对象")
public class EmailDelOutboundError {

    @ColumnWidth(30)
    @ExcelProperty(index = 0, value = "客户代码")
    private String customCode;
    @ColumnWidth(30)
    @ExcelProperty(index = 1, value = "单号")
    private String orderNo;
    @ColumnWidth(50)
    @ExcelProperty(index = 2, value = "异常描述WMS")
    private String exceptionMessageWms;
}
