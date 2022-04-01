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

    @ExcelProperty(value = {"出库单号", "Outbound Order Number"})
    private String orderNo;

    @ExcelProperty(value = {"跟踪号", "Tracking Number"})
    private String trackingNo;

    @ExcelProperty(value = {"SKU", "SKU"})
    private String sku;

    @ExcelProperty(value = {"英文申报名称", "Declared Name (English)"})
    private String declaredNameEn;

    @ExcelProperty(value = {"件数", "Qty"})
    private Long qty;

    @ExcelProperty(value = {"产品属性", "Product Attribute"})
    private String productAttributeName;

}
