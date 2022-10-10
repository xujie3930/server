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
public class DelOutboundExportListEnVO implements Serializable {

    @Excel(name = "state",width = 15)
    private String stateName;

    @Excel(name = "Delivery order No",width = 30)
    private String orderNo;

    @Excel(name = "Tracking No",width = 30)
    private String trackingNo;

    @Excel(name = "Seller Code",width = 30)
    private String sellerCode;

    @Excel(name = "Warehouse Code")
    private String warehouseName;

    @Excel(name = "RefNo",width = 15)
    private String refNo;

    @Excel(name = "Outbound method",width = 30)
    private String orderTypeName;

    @Excel(name = "Logistics service",width = 30)
    private String shipmentRule;

    @Excel(name = "Addressee's name",width = 30)
    private String consignee;

    @Excel(name = "Address1",width = 15)
    private String street1;

    @Excel(name = "Address2",width = 30)
    private String street2;

    @Excel(name = "State/province",width = 30)
    private String stateOrProvince;

    @Excel(name = "City",width = 30)
    private String city;

    @Excel(name = "Postcode",width = 30)
    private String postCode;

    @Excel(name = "Country",width = 30)
    private String country;

    @Excel(name = "Contact Information",width = 15)
    private String phoneNo;

    @Excel(name = "E-mail",width = 30)
    private String email;

    @Excel(name = "Recipient tax number",width = 15)
    private String ioss;

    @Excel(name = "weight",width = 15)
    private Double weight;

    @Excel(name = "Bubble weight",width = 15)
    private Double bubbleWeight;

    @Excel(name = "Billing weight",width = 15)
    private BigDecimal calcWeight;

    @Excel(name = "Specification (CM)",width = 15)
    private String specifications;

    @Excel(name = "Time of arraignment",width = 15)
    private Date bringVerifyTime;

    @Excel(name = "Delivery time",width = 15)
    private Date shipmentsTime;

    @Excel(name = "Exception state",width = 15)
    private String exceptionStateName;

    @Excel(name = "Exception description",width = 30)
    private String exceptionMessage;

    @Excel(name = "COD",width = 15)
    private BigDecimal codAmount;

    @Excel(name = "Remark",width = 30)
    private String remark;

    @Excel(name = "Track state",width = 15)
    private String trackingStatusName;

    @Excel(name = "Latest track",width = 30)
    private String trackingDescription;

    @Excel(name = "Latest Time",width = 30)
    private String trackingTime;


}
