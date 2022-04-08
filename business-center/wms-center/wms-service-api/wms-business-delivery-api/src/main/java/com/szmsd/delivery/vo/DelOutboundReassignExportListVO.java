package com.szmsd.delivery.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ColumnWidth(15)
public class DelOutboundReassignExportListVO implements Serializable {

    @ExcelProperty(value = {"预报单号", "RefNo"})
    private String refNo;

    @ExcelProperty(value = {"客户代码", "Seller Code"})
    private String sellerCode;

    @ExcelProperty(value = {"出库单号", "Delivery order No"})
    private String orderNo;

    @ExcelProperty(value = {"跟踪号", "Tracking No"})
    private String trackingNo;

    @ExcelProperty(value = {"处理点/仓库", "Warehouse Code"})
    private String warehouseName;

    @ExcelProperty(value = {"出库方式", "Outbound method"})
    private String orderTypeName;

    @ExcelProperty(value = {"状态", "state"})
    private String stateName;

    @ExcelProperty(value = {"物流服务", "Logistics service"})
    private String shipmentRule;

    @ExcelProperty(value = {"买家姓名", "Addressee's name"})
    private String consignee;

    @ExcelProperty(value = {"地址1", "Address1"})
    private String street1;

    @ExcelProperty(value = {"地址2", "Address2"})
    private String street2;

    @ExcelProperty(value = {"省份", "State/province"})
    private String stateOrProvince;

    @ExcelProperty(value = {"城市", "City"})
    private String city;

    @ExcelProperty(value = {"邮编", "Postcode"})
    private String postCode;

    @ExcelProperty(value = {"国家", "Country"})
    private String country;

    @ExcelProperty(value = {"电话", "Contact Information"})
    private String phoneNo;

    @ExcelProperty(value = {"邮箱", "E-mail"})
    private String email;

    @ExcelProperty(value = {"COD", "COD"})
    private BigDecimal codAmount;

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

}
