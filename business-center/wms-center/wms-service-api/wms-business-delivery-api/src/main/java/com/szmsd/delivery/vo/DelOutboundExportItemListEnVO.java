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
public class DelOutboundExportItemListEnVO implements Serializable {

    @Excel(name = "Outbound Order Number",width = 28)
    private String orderNo;

    @Excel(name = "Tracking Number",width = 28)
    private String trackingNo;

    @ExcelProperty(value = {"SKU", "SKU"})
    @Excel(name = "SKU",width = 14)
    private String sku;

    @Excel(name = "Declared Name (English)",width = 28)
    private String declaredNameEn;

    @ExcelProperty(value = {"Declared Name (Chinese)", "Declared Name (Chinese)"})
    @Excel(name = "Declared Name (Chinese)",width = 28)
    private String productNameChinese;

    @Excel(name = "Qty",width = 4)
    private Long qty;

    @Excel(name = "Product Attribute",width = 28)
    private String productAttributeName;



}
