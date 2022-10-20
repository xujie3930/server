package com.szmsd.delivery.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
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
    @Excel(name = "出库单号",width = 28)
    private String orderNo;

    @ExcelProperty(value = {"跟踪号", "Tracking Number"})
    @Excel(name = "跟踪号",width = 28)
    private String trackingNo;

    @ExcelProperty(value = {"SKU", "SKU"})
    @Excel(name = "SKU",width = 14)
    private String sku;

    @ExcelProperty(value = {"英文申报名称", "Declared Name (English)"})
    @Excel(name = "英文申报名称",width = 28)
    private String declaredNameEn;

    @ExcelProperty(value = {"中文申报品名", "中文申报品名"})
    @Excel(name = "中文申报品名",width = 28)
    private String productNameChinese;

    @ExcelProperty(value = {"件数", "Qty"})
    @Excel(name = "件数",width = 4)
    private Long qty;

    @ExcelProperty(value = {"产品属性", "Product Attribute"})
    @Excel(name = "产品属性",width = 28)
    private String productAttributeName;

}
