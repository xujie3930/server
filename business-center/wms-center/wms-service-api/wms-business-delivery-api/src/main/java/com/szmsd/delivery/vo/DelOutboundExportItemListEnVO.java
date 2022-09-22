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

    @Excel(name = "Outbound Order Number",width = 30)
    private String orderNo;

    @Excel(name = "Tracking Number",width = 30)
    private String trackingNo;

    @ExcelProperty(value = {"SKU", "SKU"})
    @Excel(name = "SKU",width = 15)
    private String sku;

    @Excel(name = "Declared Name (English)",width = 30)
    private String declaredNameEn;

    @Excel(name = "Qty",width = 15)
    private Long qty;

    @Excel(name = "Product Attribute",width = 30)
    private String productAttributeName;

}
