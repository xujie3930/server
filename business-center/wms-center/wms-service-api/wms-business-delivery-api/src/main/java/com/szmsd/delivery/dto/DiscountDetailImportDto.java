package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "DiscountDetailImportDto", description = "折扣方案-导入关联产品")
public class DiscountDetailImportDto {


    @Excel(name = "产品代码")
    private String productCode;

    @Excel(name = "费用类型")
    private String chargeType;

    @Excel(name = "处理点")
    private String warehourse;

    @Excel(name = "低于最小边长范围")
    private String minPackingLimitStr;

    @Excel(name = "超出最大边长范围")
    private String packingLimitStr;

    @Excel(name = "最小计费重量")
    private BigDecimal minWeight;

    @Excel(name = "最大计费重量")
    private BigDecimal maxWeight;

    @Excel(name = "最小物理重量")
    private BigDecimal minPhysicalWeight;

    @Excel(name = "最大物理重量")
    private BigDecimal maxPhysicalWeight;


    @Excel(name = "重量单位")
    private String weightUnit;


    @Excel(name = "最小L+(W+H)*2")
    private BigDecimal minVolumeLong;


    @Excel(name = "最大L+(W+H)*2")
    private BigDecimal volumeLong;


    @Excel(name = "L*W*H")
    private BigDecimal minVolume;

    @Excel(name = "L+W+H")
    private BigDecimal minPerimeter;

    @Excel(name = "偏远地区模板")
    private String remoteModel;

    @Excel(name = "分区名称")
    private String zoneName;

    @Excel(name = "起始价格")
    private BigDecimal startPrice;

    @Excel(name = "续重重量")
    private BigDecimal deltaWeightPerStage;

    @Excel(name = "续重价格")
    private BigDecimal deltaChargePerStage;

    @Excel(name = "续数数量")
    private Integer detalNumberPerQuantity;

    @Excel(name = "续数价格")
    private BigDecimal detalChargePerQuantity;

    @Excel(name = "最低价格")
    private BigDecimal minPrice;

    @Excel(name = "最高价格")
    private BigDecimal maxPrice;

    @Excel(name = "计费方式")
    private String chargeRuleType;

    @Excel(name = "折扣")
    private BigDecimal percentage;

    @Excel(name = "生效时间")
    private Date beginTimeDate;

    @Excel(name = "截止时间")
    private Date endTimeDate;

    @Excel(name = "销售价格级别")
    private String null1;























}
