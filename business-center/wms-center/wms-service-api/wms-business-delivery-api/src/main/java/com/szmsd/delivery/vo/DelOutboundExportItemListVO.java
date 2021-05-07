package com.szmsd.delivery.vo;

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
public class DelOutboundExportItemListVO implements Serializable {

    @ExcelProperty(value = "出库单号")
    private String orderNo;

    @ExcelProperty(value = "跟踪号")
    private String trackingNo;

    @ExcelProperty(value = "SKU")
    private String sku;

    @ExcelProperty(value = "英文申报名称")
    private String declaredNameEn;

    @ExcelProperty(value = "件数")
    private Long qty;

    @ExcelProperty(value = "产品属性")
    private String productAttributeName;

}
