package com.szmsd.chargerules.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
@ApiModel(value = "DiscountDetailExportListDto", description = "折扣方案-导出关联产品")
public class DiscountDetailExportListVO {


    @ExcelProperty(value = "方案名称")
    private String name;

    @ExcelProperty("产品代码")
    private String productCode;

    @ExcelProperty("费用类型")
    private String chargeType;

    @ExcelProperty("处理点")
    private String warehourse;


    @ExcelProperty("低于最小边长范围")
    private String minPackingLimitStr;

    @ExcelProperty("超出最大边长范围")
    private String packingLimitStr;

    @ExcelProperty("最小计费重量")
    private BigDecimal minWeight;

    @ExcelProperty("最大计费重量")
    private BigDecimal maxWeight;

    @ExcelProperty("最小物理重量")
    private BigDecimal minPhysicalWeight;

    @ExcelProperty("最大物理重量")
    private BigDecimal maxPhysicalWeight;


    @ExcelProperty("重量单位")
    private String weightUnit;


    @ExcelProperty("最小L+(W+H)*2")
    private BigDecimal minVolumeLong;


    @ExcelProperty("最大L+(W+H)*2")
    private BigDecimal volumeLong;


    @ExcelProperty("L*W*H")
    private BigDecimal minVolume;

    @ExcelProperty("L+W+H")
    private BigDecimal minPerimeter;

    @ExcelProperty("偏远地区模板")
    private String remoteModel;

    @ExcelProperty("分区名称")
    private String zoneName;

    @ExcelProperty("起始价格")
    private BigDecimal startPrice;

    @ExcelProperty("续重重量")
    private BigDecimal deltaWeightPerStage;

    @ExcelProperty("续重价格")
    private BigDecimal deltaChargePerStage;

    @ExcelProperty("续数数量")
    private Integer detalNumberPerQuantity;

    @ExcelProperty("续数价格")
    private BigDecimal detalChargePerQuantity;

    @ExcelProperty("最低价格")
    private BigDecimal minPrice;

    @ExcelProperty("最高价格")
    private BigDecimal maxPrice;

    @ExcelProperty("计费方式")
    private String chargeRuleType;

    @ExcelProperty("折扣")
    private BigDecimal percentage;

    @ExcelProperty("生效时间")
    private String beginTime;

    @ExcelProperty("截止时间")
    private String endTime;

    @ExcelProperty("销售价格级别")
    private String null1;























}
