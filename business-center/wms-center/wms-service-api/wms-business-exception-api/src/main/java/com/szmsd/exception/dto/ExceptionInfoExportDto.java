package com.szmsd.exception.dto;

import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.afterturn.easypoi.excel.annotation.Excel;


import java.util.List;

@Data
public class ExceptionInfoExportDto {

    @Excel(name="异常号",width = 30,needMerge = true)
    @ApiModelProperty(value = "异常号")
    private String exceptionNo;

    //@ExcelIgnore
    //@Excel(name="状态",width = 30)
    @ApiModelProperty(value = "状态")
    private String state;

    @Excel(name="状态",width = 30,needMerge = true)
    @ApiModelProperty(value = "状态名称")
    private String stateName;

    @Excel(name="异常类型名称",width = 30,needMerge = true)
    @ApiModelProperty(value = "异常类型名称")
    private String exceptionTypeName;

    @Excel(name="订单类型名称",width = 30,needMerge = true)
    @ApiModelProperty(value = "订单类型名称")
    private String orderTypeName;

    @Excel(name="仓库备注",width = 30,needMerge = true)
    @ApiModelProperty(value = "仓库备注")
    private String remark;

    @Excel(name="订单号",width = 30,needMerge = true)
    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @Excel(name="异常描述",width = 30,needMerge = true)
    @ApiModelProperty(value = "异常描述")
    private String exceptionMessage;

    @Excel(name="物流服务",width = 30,needMerge = true)
    @ApiModelProperty(value = "发货规则（也就是物流承运商，必须填写指定值，例如Fedex, USPS等，相同代表一起交货。）")
    private String shipmentRule;

    @Excel(name="收件人",width = 30,needMerge = true)
    @ApiModelProperty(value = "收件人")
    private String consignee;

    @Excel(name="地址1",width = 30,needMerge = true)
    @ApiModelProperty(value = "街道1")
    private String street1;

    @Excel(name="地址2",width = 30,needMerge = true)
    @ApiModelProperty(value = "街道2")
    private String street2;

    @Excel(name="城市",width = 30,needMerge = true)
    @ApiModelProperty(value = "城市")
    private String city;

    @Excel(name="省份/州",width = 30,needMerge = true)
    @ApiModelProperty(value = "省份/州")
    private String stateOrProvince;

    @Excel(name="国家",width = 30,needMerge = true)
    @ApiModelProperty(value = "国家名称")
    private String country;

    @Excel(name="邮编",width = 30,needMerge = true)
    @ApiModelProperty(value = "邮编")
    private String postCode;

    @Excel(name="联系方式",width = 30,needMerge = true)
    @ApiModelProperty(value = "电话号码")
    private String phoneNo;

    @Excel(name="电子邮箱",width = 30,needMerge = true)
    @ApiModelProperty(value = "邮箱")
    private String email;

    @ApiModelProperty(value = "单类类型")
    private String orderType;



    /**
     集合
     **/
    @ExcelCollection(name = "出库单sku明细")
    private List<ExceptionInfoDetailExportDto> exceptionInfoDetailExportDtoList;

}
