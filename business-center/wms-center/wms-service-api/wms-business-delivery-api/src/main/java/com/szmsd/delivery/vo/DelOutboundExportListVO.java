package com.szmsd.delivery.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ColumnWidth(15)
public class DelOutboundExportListVO implements Serializable {

    @ExcelProperty(value = "客户代码")
    private String sellerCode;

    @ExcelProperty(value = "处理点/仓库")
    private String warehouseName;

    @ExcelProperty(value = "客户经理")
    private String accountManager;

    @ExcelProperty(value = "客服")
    private String customerService;

    @ExcelProperty(value = "处理号")
    private String processingNumber;

    @ExcelProperty(value = "跟踪号")
    private String trackingNo;

    @ExcelProperty(value = "(原)跟踪号")
    private String oldTrackingNo;

    @ExcelProperty(value = "物流单号")
    private String shipmentOrderNumber;

    @ExcelProperty(value = "RefNo")
    private String refOrderNo;

    @ExcelProperty(value = "供应商服务代码")
    private String shipmentService;

    @ExcelProperty(value = "提审时间")
    private String bringVerifyTime;

    @ExcelProperty(value = "到仓时间")
    private String arrivalTime;

    @ExcelProperty(value = "发货时间")
    private String deliveryTime;

    @ExcelProperty(value = "异常状态")
    private String exceptionStateName;

    @ExcelProperty(value = "服务产品代码")
    private String shipmentRule;

    @ExcelProperty(value = "箱数")
    private String boxNumber;

    @ExcelProperty(value = "重量")
    private Double weight;

    @ExcelProperty(value = "计费重")
    private BigDecimal calcWeight;

    @ExcelProperty(value = "实际重量")
    private Double actualWeight;

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

    @ExcelProperty(value = "申报名称")
    private String declaredName;

    @ExcelProperty(value = "申报价值")
    private BigDecimal declaredValue;

    @ExcelProperty(value = "电话")
    private String phoneNo;

    @ExcelProperty(value = "收件人税号")
    private String consigneeTaxNo;

    @ExcelProperty(value = "产品名称")
    private String productName;

    @ExcelProperty(value = "规格(cm)")
    private String specifications;

}
