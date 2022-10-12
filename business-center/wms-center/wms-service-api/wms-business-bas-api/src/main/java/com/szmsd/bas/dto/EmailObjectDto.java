package com.szmsd.bas.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "", description = "EmailObjectDto对象")
public class EmailObjectDto {
    @ColumnWidth(30)
    @ExcelProperty(index = 0, value = "orderNo")
    private String orderNo;

    @ColumnWidth(30)
    @ExcelProperty(index = 1, value = "trackingNo")
    private String trackingNo;
}
