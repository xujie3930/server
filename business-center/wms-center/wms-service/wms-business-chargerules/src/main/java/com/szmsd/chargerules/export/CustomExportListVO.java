package com.szmsd.chargerules.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ColumnWidth(15)
public class CustomExportListVO implements Serializable {

    @ExcelProperty(value = "方案名称")
    private String name;

    @ExcelProperty("客户代码")
    private String clientCode;

    @ExcelProperty("生效时间")
    private String beginTime;

    @ExcelProperty("截止时间")
    private String endTime;

    @ExcelProperty("是否有效")
    private String isValidStr;


}
