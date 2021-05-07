package com.szmsd.delivery.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ColumnWidth(15)
public class DelOutboundExportListVO implements Serializable {

    @ExcelProperty(value = "状态")
    private String stateName;

    @ExcelProperty(value = "出库单号")
    private String orderNo;

    @ExcelProperty(value = "跟踪号")
    private String trackingNo;

    @ExcelProperty(value = "客户代码")
    private String sellerCode;

    @ExcelProperty(value = "处理点/仓库")
    private String warehouseName;

    @ExcelProperty(value = "RefNo")
    private String refOrderNo;

    @ExcelProperty(value = "出库方式")
    private String orderTypeName;

    @ExcelProperty(value = "物流服务")
    private String shipmentRule;

    @ExcelProperty(value = "买家姓名")
    private String consignee;

    @ExcelProperty(value = "地址1")
    private String street1;

    @ExcelProperty(value = "地址2")
    private String street2;

    @ExcelProperty(value = "省份")
    private String stateOrProvince;

    @ExcelProperty(value = "城市")
    private String city;

    @ExcelProperty(value = "邮编")
    private String postCode;

    @ExcelProperty(value = "国家")
    private String country;

    @ExcelProperty(value = "电话")
    private String phoneNo;

    @ExcelProperty(value = "收件人税号")
    private String consigneeTaxNo;

    @ExcelProperty(value = "重量")
    private Double weight;

    @ExcelProperty(value = "泡重")
    private Double bubbleWeight;

    @ExcelProperty(value = "计费重")
    private BigDecimal calcWeight;

    @ExcelProperty(value = "规格(cm)")
    private String specifications;

    @ExcelProperty(value = "提审时间")
    private Date bringVerifyTime;

    @ExcelProperty(value = "发货时间")
    private Date shipmentsTime;

    @ExcelProperty(value = "异常状态")
    private String exceptionStateName;

}
