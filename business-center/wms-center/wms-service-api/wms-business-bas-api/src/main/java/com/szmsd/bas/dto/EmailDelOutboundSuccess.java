package com.szmsd.bas.dto;


import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "", description = "EmailDelOutboundSuccess对象")
public class EmailDelOutboundSuccess {

    @ColumnWidth(30)
    @ExcelProperty(index = 0, value = "订单类型")
    private String orderType;
    @ColumnWidth(30)
    @ExcelProperty(index = 1, value = "DM单号")
    private String orderNo;

    @ColumnWidth(30)
    @ExcelProperty(index = 2, value = "客户代码")
    private String customCode;

    @ColumnWidth(30)
    @ExcelProperty(index = 3, value = "物流服务")
    private String prcInterfaceProductCode;

    @ColumnWidth(30)
    @ExcelProperty(index = 4, value = "国家")
    private String country;

    @ColumnWidth(30)
    @ExcelProperty(index = 5, value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;
}
