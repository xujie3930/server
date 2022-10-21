package com.szmsd.delivery.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
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

    @ExcelProperty(value = {"状态", "state"})
    @Excel(name = "状态",width = 10)
    private String stateName;

    @ExcelProperty(value = {"出库单号", "Delivery order No"})
    @Excel(name = "出库单号",width = 28)
    private String orderNo;

    @ExcelProperty(value = {"跟踪号", "Tracking No"})
    @Excel(name = "跟踪号",width = 20)
    private String trackingNo;

    @ExcelProperty(value = {"客户代码", "Seller Code"})
    @Excel(name = "客户代码",width = 10)
    private String sellerCode;

    @ExcelProperty(value = {"处理点/仓库", "Warehouse Code"})
    @Excel(name = "处理点/仓库",width = 10)
    private String warehouseName;

    @ExcelProperty(value = {"RefNo", "RefNo"})
    @Excel(name = "RefNo",width = 18)
    private String refNo;

    @ExcelProperty(value = {"出库方式", "Outbound method"})
    @Excel(name = "出库方式",width = 10)
    private String orderTypeName;

    @ExcelProperty(value = {"物流服务", "Logistics service"})
    @Excel(name = "物流服务",width = 10)
    private String shipmentRule;

    @ExcelProperty(value = {"买家姓名", "Addressee's name"})
    @Excel(name = "买家姓名",width = 10)
    private String consignee;

    @ExcelProperty(value = {"地址1", "Address1"})
    @Excel(name = "地址1",width = 10)
    private String street1;

    @ExcelProperty(value = {"地址2", "Address2"})
    @Excel(name = "地址2",width = 10)
    private String street2;

    @ExcelProperty(value = {"省份", "State/province"})
    @Excel(name = "省份",width = 10)
    private String stateOrProvince;

    @ExcelProperty(value = {"城市", "City"})
    @Excel(name = "城市",width = 30)
    private String city;

    @ExcelProperty(value = {"邮编", "Postcode"})
    @Excel(name = "邮编",width = 10)
    private String postCode;

    @ExcelProperty(value = {"国家", "Country"})
    @Excel(name = "国家",width = 10)
    private String country;

    @ExcelProperty(value = {"电话", "Contact Information"})
    @Excel(name = "电话",width = 10)
    private String phoneNo;

    @ExcelProperty(value = {"邮箱", "E-mail"})
    @Excel(name = "邮箱",width = 28)
    private String email;

    @ExcelProperty(value = {"收件人税号", "Recipient tax number"})
    @Excel(name = "收件人税号",width = 14)
    private String ioss;

    @ExcelProperty(value = {"重量", "weight"})
    @Excel(name = "重量",width = 15)
    private Double weight;

    @ExcelProperty(value = {"泡重", "Bubble weight"})
    @Excel(name = "泡重",width = 10)
    private Double bubbleWeight;

    @ExcelProperty(value = {"计费重", "Billing weight"})
    @Excel(name = "计费重",width = 10)
    private BigDecimal calcWeight;

    @ExcelProperty(value = {"规格(cm)", "Specification (CM)"})
    @Excel(name = "规格(cm)",width = 14)
    private String specifications;

    @ExcelProperty(value = {"提审时间", "Time of arraignment"})
    @Excel(name = "提审时间",width = 18)
    private String bringVerifyTime;

    @ExcelProperty(value = {"发货时间", "Delivery time"})
    @Excel(name = "发货时间",width = 18)
    private String shipmentsTime;

    @ExcelProperty(value = {"异常状态", "Exception state"})
    @Excel(name = "异常状态",width = 10)
    private String exceptionStateName;

    @ExcelProperty(value = {"异常描述", "Exception description"})
    @Excel(name = "异常描述",width = 15)
    private String exceptionMessage;

    @ExcelProperty(value = {"COD", "COD"})
    @Excel(name = "COD",width = 10)
    private BigDecimal codAmount;

    @ExcelProperty(value = {"备注", "Remark"})
    @Excel(name = "备注",width = 18)
    private String remark;

    @ExcelProperty(value = {"轨迹状态", "Track state"})
    @Excel(name = "轨迹状态",width = 18)
    private String trackingStatusName;

    @ExcelProperty(value = {"轨迹信息描述", "Latest track"})
    @Excel(name = "轨迹信息描述",width = 18)
    private String trackingDescription;

    @ExcelProperty(value = {"轨迹时间", "Latest Date"})
    @Excel(name = "轨迹时间",width = 18)
    private String trackingTime;



}
