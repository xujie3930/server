package com.szmsd.chargerules.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ColumnWidth(15)
public class GradeDetailExportListVO implements Serializable {

    @ExcelProperty(value = "方案名称")
    private String name;

    @ExcelProperty(value = "产品代码")
    private String productCode;

    @ExcelProperty(value = "产品类别")
    private String pricingProductCategory;

    @ExcelProperty(value = "等级")
    private String grade;






}
