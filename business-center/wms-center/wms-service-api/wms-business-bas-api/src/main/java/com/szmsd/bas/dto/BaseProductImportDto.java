package com.szmsd.bas.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseProductImportDto {

    @ExcelProperty(index = 1)
    @ApiModelProperty(value = "英文申报品名")
    @Excel(name = "英文申报品名" ,type = Excel.Type.IMPORT)
    private String productName;

    @ExcelProperty(index = 0)
    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU" ,type = Excel.Type.IMPORT)
    private String code;

    @ExcelProperty(index = 10)
    @ApiModelProperty(value = "重量(g)")
    @Excel(name = "重量(g)" ,type = Excel.Type.IMPORT)
    private Double initWeight;

    @ExcelProperty(index = 11)
    @ApiModelProperty(value = "长（cm）")
    @Excel(name = "长（cm）" ,type = Excel.Type.IMPORT)
    private Double initLength;

    @ExcelProperty(index = 12)
    @ApiModelProperty(value = "宽（cm）")
    @Excel(name = "宽（cm）" ,type = Excel.Type.IMPORT)
    private Double initWidth;

    @ExcelProperty(index = 13)
    @ApiModelProperty(value = "高（cm）")
    @Excel(name = "高（cm）" ,type = Excel.Type.IMPORT)
    private Double initHeight;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "中文申报品名")
    @Excel(name = "中文申报品名" ,type = Excel.Type.IMPORT)
    private String productNameChinese;

    @ExcelProperty(index = 3)
    @ApiModelProperty(value = "申报价值(USD)")
    @Excel(name = "申报价值(USD)" ,type = Excel.Type.IMPORT)
    private Double declaredValue;

    @ExcelProperty(index = 4)
    @ApiModelProperty(value = "产品属性")
    @Excel(name = "产品属性" ,type = Excel.Type.IMPORT)
    private String productAttributeName;

    @ApiModelProperty(value = "产品属性")
    private String productAttribute;

    @ExcelProperty(index = 5)
    @ApiModelProperty(value = "电池类型")
    @Excel(name = "电池类型" ,type = Excel.Type.IMPORT)
    private String electrifiedModeName;

    @ApiModelProperty(value = "电池类型")
    private String electrifiedMode;

    @ExcelProperty(index = 6)
    @ApiModelProperty(value = "电池包装")
    @Excel(name = "电池包装" ,type = Excel.Type.IMPORT)
    private String batteryPackagingName;

    @ApiModelProperty(value = "电池包装")
    private String batteryPackaging;

    @ExcelProperty(index = 8)
    @ApiModelProperty(value = "自备包材条码")
    @Excel(name = "自备包材条码" ,type = Excel.Type.IMPORT)
    private String bindCode;

    @ApiModelProperty(value = "自备包材条码")
    private String bindCodeName;

    @ExcelProperty(index = 9)
    @ApiModelProperty(value = "物流包装要求")
    @Excel(name = "物流包装要求" ,type = Excel.Type.IMPORT)
    private String suggestPackingMaterial;

    @ApiModelProperty(value = "物流包装要求")
    private String suggestPackingMaterialCode;

    @ExcelProperty(index = 14)
    @ApiModelProperty(value = "产品说明")
    @Excel(name = "产品说明" ,type = Excel.Type.IMPORT)
    private String productDescription;

    @ExcelProperty(index = 7)
    @ApiModelProperty(value = "是否自备包材")
    @Excel(name = "是否自备包材" ,type = Excel.Type.IMPORT)
    private String havePackingMaterialName;

    @ApiModelProperty(value = "是否自备包材")
    private Boolean havePackingMaterial;

    @ApiModelProperty(value = "价格区间")
    private String priceRange;



}
