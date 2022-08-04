package com.szmsd.chargerules.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ColumnWidth(15)
public class ExclusiveGradeDetailExportListVO implements Serializable {

    @ExcelProperty(value = "产品代码")
    private String productCode;

    @ExcelProperty(value = "产品类别")
    private String pricingProductCategory;

    @ExcelProperty(value = "等级")
    private String grade;

    @ExcelProperty("国家")
    private String defaultCountry;

    @ExcelProperty("子产品代码")
    private String subProduct;

    @ExcelProperty("有效起始时间")
    private String beginTime;

    @ExcelProperty("有效结束时间")
    private String endTime;






}
