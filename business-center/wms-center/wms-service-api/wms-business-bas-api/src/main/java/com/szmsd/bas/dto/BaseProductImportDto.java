package com.szmsd.bas.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BaseProductImportDto {

    @ApiModelProperty(value = "申报名称")
    @Excel(name = "申报名称" ,type = Excel.Type.IMPORT)
    private String productName;

    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU" ,type = Excel.Type.IMPORT)
    private String code;

    @ApiModelProperty(value = "重量(g)")
    @Excel(name = "重量(g)" ,type = Excel.Type.IMPORT)
    private Double initWeight;

    @ApiModelProperty(value = "长（cm）")
    @Excel(name = "长（cm）" ,type = Excel.Type.IMPORT)
    private Double initLength;

    @ApiModelProperty(value = "宽（cm）")
    @Excel(name = "宽（cm）" ,type = Excel.Type.IMPORT)
    private Double initWidth;

    @ApiModelProperty(value = "高（cm）")
    @Excel(name = "高（cm）" ,type = Excel.Type.IMPORT)
    private Double initHeight;


    @ApiModelProperty(value = "中文申报名称")
    @Excel(name = "中文申报名称" ,type = Excel.Type.IMPORT)
    private String productNameChinese;

    @ApiModelProperty(value = "申报价值(USD)")
    @Excel(name = "申报价值(USD)" ,type = Excel.Type.IMPORT)
    private Double declaredValue;


    @ApiModelProperty(value = "产品属性")
    @Excel(name = "产品属性" ,type = Excel.Type.IMPORT)
    private String productAttributeName;

    @ApiModelProperty(value = "产品属性")
    private String productAttribute;


    @ApiModelProperty(value = "电池类型")
    @Excel(name = "电池类型" ,type = Excel.Type.IMPORT)
    private String electrifiedModeName;

    @ApiModelProperty(value = "电池类型")
    private String electrifiedMode;

    @ApiModelProperty(value = "电池包装")
    @Excel(name = "电池包装" ,type = Excel.Type.IMPORT)
    private String batteryPackagingName;

    @ApiModelProperty(value = "电池包装")
    private String batteryPackaging;

    @ApiModelProperty(value = "自备包材条码")
    @Excel(name = "自备包材条码" ,type = Excel.Type.IMPORT)
    private String bindCode;

    @ApiModelProperty(value = "自备包材条码")
    private String bindCodeName;

    @ApiModelProperty(value = "物流包装要求")
    @Excel(name = "物流包装要求" ,type = Excel.Type.IMPORT)
    private String suggestPackingMaterial;

    @ApiModelProperty(value = "物流包装要求")
    private String suggestPackingMaterialCode;


    @ApiModelProperty(value = "产品说明")
    @Excel(name = "产品说明" ,type = Excel.Type.IMPORT)
    private String productDescription;

    @ApiModelProperty(value = "是否自备包材")
    @Excel(name = "是否自备包材" ,type = Excel.Type.IMPORT)
    private String havePackingMaterialName;

    @ApiModelProperty(value = "是否自备包材")
    private Boolean havePackingMaterial;



}
