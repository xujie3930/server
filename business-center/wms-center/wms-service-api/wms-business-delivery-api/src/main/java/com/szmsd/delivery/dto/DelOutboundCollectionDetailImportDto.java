package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:23
 */
@Data
@ApiModel(value = "DelOutboundCollectionDetailImportDto", description = "DelOutboundCollectionDetailImportDto对象")
public class DelOutboundCollectionDetailImportDto implements Serializable {

    @ExcelProperty(index = 0)
    @ApiModelProperty(value = "产品编码")
    private String code;

    @ExcelProperty(index = 1)
    @ApiModelProperty(value = "英文申报品名")
    private String productName;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "中文申报品名")
    private String productNameChinese;

    @ExcelProperty(index = 3)
    @ApiModelProperty(value = "申报价值（美元）")
    private Double declaredValue;

    @ApiModelProperty(value = "产品属性编号")
    @ExcelIgnore
    private String productAttribute;

    @ExcelProperty(index = 4)
    @ApiModelProperty(value = "产品属性")
    private String productAttributeName;

    @ExcelProperty(index = 5)
    @ApiModelProperty(value = "产品说明")
    private String productDescription;

    @ExcelProperty(index = 6)
    @ApiModelProperty(value = "材质")
    private String materialQuality;

    @ExcelProperty(index = 7)
    @ApiModelProperty(value = "用途")
    private String purpose;

    @ApiModelProperty(value = "带电信息编号")
    @ExcelIgnore
    private String electrifiedMode;

    @ExcelProperty(index = 8)
    @ApiModelProperty(value = "带电信息")
    private String electrifiedModeName;

    @ApiModelProperty(value = "电池包装编号")
    @ExcelIgnore
    private String batteryPackaging;

    @ExcelProperty(index = 9)
    @ApiModelProperty(value = "电池包装")
    private String batteryPackagingName;

    @ExcelProperty(index = 10)
    @ApiModelProperty(value = "海关编码")
    private String hsCode;



}
