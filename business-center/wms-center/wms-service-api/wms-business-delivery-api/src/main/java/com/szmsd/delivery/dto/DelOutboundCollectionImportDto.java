package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:51
 */
@Data
@ApiModel(value = "DelOutboundCollectionImportDto", description = "DelOutboundCollectionImportDto对象")
public class DelOutboundCollectionImportDto implements Serializable {

    @ApiModelProperty(value = "订单序号\n" +
            "（必填）")
    private Integer sort;

    @ApiModelProperty(value = "交货仓库\n" +
            "（必填）")
    private String warehouseCode;

    @ApiModelProperty(value = "物流服务\n" +
            "（必填）")
    private String shipmentRule;


    @ApiModelProperty(value = "增值税号\n" +
            "（非必填）")
    private String ioss;


    @ApiModelProperty(value = "COD\n" +
            "（非必填）")
    private BigDecimal codAmount;

    @ApiModelProperty(value = "收件人姓名\n" +
            "（必填）")
    private String consignee;

    @ApiModelProperty(value = "街道1\n" +
            "（必填）")
    private String street1;

    @ApiModelProperty(value = "街道2\n" +
            "（非必填）")
    private String street2;

    @ApiModelProperty(value = "城镇/城市\n" +
            "（必填）")
    private String city;

    @ApiModelProperty(value = "州/省\n" +
            "（必填）")
    private String stateOrProvince;


    @ApiModelProperty(value = "国家\n" +
            "（必填）")
    private String country;

    @ApiModelProperty(value = "邮编\n" +
            "（必填）")
    private String postCode;


    @ApiModelProperty(value = "联系电话\n" +
            "（非必填）")
    private String phoneNo;


    @ApiModelProperty(value = "电子邮箱\n" +
            "（非必填）")
    private String email;


    @ApiModelProperty(value = "RefNo\n" +
            "（非必填）")
    private String refNo;

    @ApiModelProperty(value = "包裹重量（g）\n" +
            "（非必填）")
    private Double weight;

    @ApiModelProperty(value = "包裹尺寸（长/cm）\n" +
            "（非必填）")
    private Double length;

    @ApiModelProperty(value = "包裹尺寸（宽/cm）\n" +
            "（非必填）")
    private Double width;

    @ApiModelProperty(value = "包裹尺寸（高/cm）\n" +
            "（非必填）")
    private Double height;


    @ApiModelProperty(value = "备注\n" +
            "（非必填）")
    private String remark;


}
