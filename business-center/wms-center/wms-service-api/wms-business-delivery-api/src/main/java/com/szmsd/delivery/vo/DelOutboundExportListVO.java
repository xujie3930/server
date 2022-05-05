package com.szmsd.delivery.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import io.swagger.annotations.ApiModelProperty;
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
    private String stateName;

    @ExcelProperty(value = {"出库单号", "Delivery order No"})
    private String orderNo;

    @ExcelProperty(value = {"跟踪号", "Tracking No"})
    private String trackingNo;

    @ExcelProperty(value = {"客户代码", "Seller Code"})
    private String sellerCode;

    @ExcelProperty(value = {"处理点/仓库", "Warehouse Code"})
    private String warehouseName;

    @ExcelProperty(value = {"RefNo", "RefNo"})
    private String refNo;

    @ExcelProperty(value = {"出库方式", "Outbound method"})
    private String orderTypeName;

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

    @ExcelProperty(value = {"收件人税号", "Recipient tax number"})
    private String ioss;

    @ExcelProperty(value = {"重量", "weight"})
    private Double weight;

    @ExcelProperty(value = {"泡重", "Bubble weight"})
    private Double bubbleWeight;

    @ExcelProperty(value = {"计费重", "Billing weight"})
    private BigDecimal calcWeight;

    @ExcelProperty(value = {"规格(cm)", "Specification (CM)"})
    private String specifications;

    @ExcelProperty(value = {"提审时间", "Time of arraignment"})
    private Date bringVerifyTime;

    @ExcelProperty(value = {"发货时间", "Delivery time"})
    private Date shipmentsTime;

    @ExcelProperty(value = {"异常状态", "Exception state"})
    private String exceptionStateName;

    @ExcelProperty(value = {"异常描述", "Exception description"})
    private String exceptionMessage;

    @ExcelProperty(value = {"COD", "COD"})
    private BigDecimal codAmount;

    @ExcelProperty(value = {"备注", "Remark"})
    private String remark;

    @ExcelProperty(value = {"轨迹状态", "Track state"})
    private String trackingStatusName;

    @ExcelProperty(value = {"轨迹信息描述", "Latest track"})
    private String trackingDescription;


}
