package com.szmsd.pack.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
@ApiModel(value = "揽收导入模板")
public class PackageMangQueryExcle {

    @ExcelProperty(index = 0)
    @Excel(name = "收货单号" ,width = 30, type = Excel.Type.ALL)
    private String orderNo;

    @ExcelProperty(index = 1)
    @Excel(name = "客户代码",width = 30, type = Excel.Type.ALL)
    private String sellerCode;

    @ExcelProperty(index = 2)
    @Excel(name = "省",width = 30, type = Excel.Type.ALL)
    private String provinceNameZh;

    @ExcelProperty(index = 3)
    @Excel(name = "市",width = 30, type = Excel.Type.ALL)
    private String cityNameZh;

    @ExcelProperty(index = 4)
    @Excel(name = "区",width = 30, type = Excel.Type.ALL)
    private String districtNameZh;

    @ExcelProperty(index = 5)
    @Excel(name = "收货地址",width = 30, type = Excel.Type.ALL)
    private String deliveryAddress;

    @ExcelProperty(index = 6)
    @Excel(name = "电话",width = 30, type = Excel.Type.ALL)
    private String linkPhone;

    @ExcelProperty(index = 7)
    @Excel(name = "联系人",width = 30, type = Excel.Type.ALL)
    private String linkUserName;

    @ExcelProperty(index = 8)
    @Excel(name = "司机姓名",width = 30, type = Excel.Type.ALL)
    private String driverName;

    @ExcelProperty(index = 9)
    @Excel(name = "司机电话",width = 30, type = Excel.Type.ALL)
    private String driverPhone;

    @ExcelProperty(index = 10)
    @Excel(name = "司机车牌",width = 30, type = Excel.Type.ALL)
    private String driverLicensePlate;


    @ExcelProperty(index = 11)
    @Excel(name = "状态",width = 30, type = Excel.Type.ALL)
    private String exportType;

    @ExcelProperty(index = 12)
    @Excel(name = "货物件数",width = 30, type = Excel.Type.ALL)
    private String pieceNumber;

    @ExcelProperty(index = 13)
    @Excel(name = "货物方数",width = 30, type = Excel.Type.ALL)
    private String squareNumber;

    @ExcelProperty(index = 14)
    @Excel(name = "货物袋数",width = 30, type = Excel.Type.ALL)
    private String bagNumber;

    @ExcelProperty(index = 15)
    @Excel(name = "请求收货时间",width = 30, type = Excel.Type.ALL)
    private String expectedDeliveryTime;

    @ExcelProperty(index = 16)
    @Excel(name = "客户备注",width = 30, type = Excel.Type.ALL)
    private String remark;

    @ExcelProperty(index = 17)
    @Excel(name = "DM反馈原因",width = 30, type = Excel.Type.ALL)
    private String dmRemark;
}
