package com.szmsd.delivery.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ColumnWidth(15)
public class DelOutboundReassignExportListVO implements Serializable {

    @ExcelProperty(value = {"预报单号", "RefNo"}, index = 0)
    private String refNo;

    @ExcelProperty(value = {"客户代码", "Seller Code"}, index = 1)
    private String sellerCode;

    @ExcelProperty(value = {"出库单号", "Delivery order No"}, index = 2)
    private String orderNo;

    @ExcelProperty(value = {"跟踪号", "Tracking No"}, index = 3)
    private String trackingNo;

    @ExcelProperty(value = {"处理点/仓库", "Warehouse Code"}, index = 4)
    private String warehouseName;

    @ExcelProperty(value = {"出库方式", "Outbound method"}, index = 5)
    private String orderTypeName;

    @ExcelProperty(value = {"状态", "state"}, index = 6)
    private String stateName;

    @ExcelProperty(value = {"物流服务", "Logistics service"}, index = 7)
    private String shipmentRule;

    @ExcelProperty(value = {"买家姓名", "Addressee's name"}, index = 8)
    private String consignee;

    @ExcelProperty(value = {"地址1", "Address1"}, index = 9)
    private String street1;

    @ExcelProperty(value = {"地址2", "Address2"}, index = 10)
    private String street2;

    @ExcelProperty(value = {"省份", "State/province"}, index = 11)
    private String stateOrProvince;

    @ExcelProperty(value = {"城市", "City"}, index = 12)
    private String city;

    @ExcelProperty(value = {"邮编", "Postcode"}, index = 13)
    private String postCode;

    @ExcelProperty(value = {"国家", "Country"}, index = 14)
    private String country;

    @ExcelProperty(value = {"电话", "Contact Information"}, index = 15)
    private String phoneNo;

    @ExcelProperty(value = {"邮箱", "E-mail"}, index = 16)
    private String email;

    @ExcelProperty(value = {"COD", "COD"}, index = 17)
    private BigDecimal codAmount;

    @ExcelProperty(value = {"增值税号", "IOSS"}, index = 18)
    private String ioss;

    @ExcelProperty(value = {"异常信息", "Exception Message"})
    private String exceptionMessage;

    @ExcelProperty(value = {"参考号", "RefNo"})
    private String refNo2;

    @ExcelProperty(value = {"备注", "Remark"})
    private String remark;

    @ExcelProperty(value = {"轨迹状态", "Tracking Status"})
    private String trackingStatusName;

    @ExcelProperty(value = {"最新轨迹", "Tracking Description"})
    private String trackingDescription;

    @ExcelProperty(value = {"创建时间", "Create Time"})
    private String createTime;

    @ExcelIgnore
    private String countryCode;
}
