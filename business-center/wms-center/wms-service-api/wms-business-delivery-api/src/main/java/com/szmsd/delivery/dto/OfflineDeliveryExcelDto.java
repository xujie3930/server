package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OfflineDeliveryExcelDto implements Serializable {

    @ApiModelProperty(value = "跟踪号")
    @ExcelProperty(value = "跟踪号",index = 0)
    private String trackingNo;

    @ApiModelProperty(value = "refNo")
    @ExcelProperty(value = "跟踪号",index = 1)
    private String refNo;

    @ApiModelProperty(value = "平台标记单号")
    @ExcelProperty(value = "平台标记单号",index = 2)
    private String amazonLogisticsRouteId;

    @ApiModelProperty(value = "买家")
    @ExcelProperty(value = "买家",index = 3)
    private String sellerCode;

    @ApiModelProperty(value = "供应商")
    @ExcelProperty(value = "供应商",index = 4)
    private String supplierName;

    @ApiModelProperty(value = "仓库代码")
    @ExcelProperty(value = "仓库代码",index = 5)
    private String warehouseCode;

    @ApiModelProperty(value = "发货服务名称")
    @ExcelProperty(value = "发货服务名称",index = 6)
    private String shipmentService;

    @ApiModelProperty(value = "客户代码")
    @ExcelProperty(value = "客户代码",index = 7)
    private String customCode;

    @ApiModelProperty(value = "街道1")
    @ExcelProperty(value = "地址1",index = 8)
    private String street1;

    @ApiModelProperty(value = "街道2")
    @ExcelProperty(value = "地址2",index = 9)
    private String street2;

    @ApiModelProperty(value = "门牌号")
    @ExcelProperty(value = "门牌号",index = 10)
    private String houseNo;

    @ApiModelProperty(value = "城市或者省份")
    @ExcelProperty(value = "城市或者省份",index = 11)
    private String stateOrProvince;

    @ApiModelProperty(value = "城市")
    @ExcelProperty(value = "城市",index = 12)
    private String city;

    @ApiModelProperty(value = "邮编")
    @ExcelProperty(value = "邮编",index = 13)
    private String postCode;

    @ApiModelProperty(value = "国家代码")
    @ExcelProperty(value = "国家代码",index = 14)
    private String countryCode;

    @ApiModelProperty(value = "电话号码")
    @ExcelProperty(value = "电话号码",index = 15)
    private String phoneNo;

    @ApiModelProperty(value = "邮箱")
    @ExcelProperty(value = "邮箱",index = 16)
    private String email;

    @ApiModelProperty(value = "收件人税号")
    @ExcelProperty(value = "收件人税号",index = 17)
    private String taxNumber;

    @ApiModelProperty(value = "cod")
    @ExcelProperty(value = "cod",index = 18)
    private String cod;

    @ApiModelProperty(value = "重量g")
    @ExcelProperty(value = "重量g",index = 19)
    private BigDecimal weight;

    @ApiModelProperty(value = "计费重")
    @ExcelProperty(value = "计费重",index = 20)
    private BigDecimal calcWeight;

    @ApiModelProperty(value = "规格")
    @ExcelProperty(value = "规格",index = 21)
    private String specifications;

    @ApiModelProperty(value = "提审时间")
    @ExcelProperty(value = "提审时间",index = 22)
    private Date bringTime;

    @ApiModelProperty(value = "发货时间")
    @ExcelProperty(value = "发货时间",index = 23)
    private Date deliveryTime;

    @ApiModelProperty(value = "备注")
    @ExcelProperty(value = "备注",index = 24)
    private String remark;
}
