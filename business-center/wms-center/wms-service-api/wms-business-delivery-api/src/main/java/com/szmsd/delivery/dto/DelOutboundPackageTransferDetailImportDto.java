package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-04-09 18:51
 */
@Data
@ApiModel(value = "DelOutboundPackageTransferDetailImportDto", description = "DelOutboundPackageTransferDetailImportDto对象")
public class DelOutboundPackageTransferDetailImportDto implements Serializable {

    @ApiModelProperty(value = "订单序号\n" +
            "（必填）")
    private Integer sort;

    @ApiModelProperty(value = "英文申报品名\n" +
            "（必填）")
    private String productName;

    @ApiModelProperty(value = "中午申报品名\n" +
            "（必填）")
    private String productNameChinese;

    @ApiModelProperty(value = "申报价值（美元）\n" +
            "（必填）")
    private Double declaredValue;

    @ApiModelProperty(value = "出库数量\n" +
            "（必填）")
    private Integer qty;

    @ApiModelProperty(value = "产品属性编号")
    @ExcelIgnore
    private String productAttribute;

    @ApiModelProperty(value = "产品属性\n" +
            "（必填）")
    private String productAttributeName;

    @ApiModelProperty(value = "带电信息编号")
    @ExcelIgnore
    private String electrifiedMode;

    @ApiModelProperty(value = "带电信息")
    private String electrifiedModeName;

    @ApiModelProperty(value = "电池包装编号")
    @ExcelIgnore
    private String batteryPackaging;

    @ApiModelProperty(value = "电池包装\n" +
            "（非必填）")
    private String batteryPackagingName;

    @ApiModelProperty(value = "海关编码\n" +
            "（非必填）")
    private String hsCode;
/*
    @ApiModelProperty(value = "产品描述")
    private String productDescription;*/
}
