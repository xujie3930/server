package com.szmsd.bas.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class BaseProductImportTemplateDto {


    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU" ,type = Excel.Type.EXPORT)
    private String code;

    @ApiModelProperty(value = "英文申报品名")
    @Excel(name = "英文申报品名" ,type = Excel.Type.EXPORT)
    private String productName;

    @ApiModelProperty(value = "中文申报品名")
    @Excel(name = "中文申报品名" ,type = Excel.Type.EXPORT)
    private String productNameChinese;

    @ApiModelProperty(value = "申报价值(USD)")
    @Excel(name = "申报价值(USD)" ,type = Excel.Type.EXPORT)
    private Double declaredValue;


    @ApiModelProperty(value = "产品属性")
    @Excel(name = "产品属性" ,type = Excel.Type.EXPORT,combo = {"普货","带电","液体","粉末","带磁"})
    private String productAttributeName;



    @ApiModelProperty(value = "电池类型")
    @Excel(name = "电池类型" ,type = Excel.Type.EXPORT,combo = {"干电池","纽扣电池","其他电池"})
    private String electrifiedModeName;

    @ApiModelProperty(value = "电池包装")
    @Excel(name = "电池包装" ,type = Excel.Type.EXPORT,combo = {"内置","配套"})
    private String batteryPackagingName;

    @ApiModelProperty(value = "是否自备包材")
    @Excel(name = "是否自备包材" ,type = Excel.Type.EXPORT,combo = {"是","否"})
    private String havePackingMaterialName;

    @ApiModelProperty(value = "自备包材条码")
    @Excel(name = "自备包材条码" ,type = Excel.Type.EXPORT)
    private String bindCode;

    @ApiModelProperty(value = "物流包装要求")
    @Excel(name = "物流包装要求" ,type = Excel.Type.EXPORT)
    private String suggestPackingMaterial;

    @ApiModelProperty(value = "重量(g)")
    @Excel(name = "重量(g)" ,type = Excel.Type.EXPORT)
    private Double initWeight;

    @ApiModelProperty(value = "长（cm）")
    @Excel(name = "长（cm）" ,type = Excel.Type.EXPORT)
    private Double initLength;

    @ApiModelProperty(value = "宽（cm）")
    @Excel(name = "宽（cm）" ,type = Excel.Type.EXPORT)
    private Double initWidth;

    @ApiModelProperty(value = "高（cm）")
    @Excel(name = "高（cm）" ,type = Excel.Type.EXPORT)
    private Double initHeight;


    @ApiModelProperty(value = "产品说明")
    @Excel(name = "产品说明" ,type = Excel.Type.EXPORT)
    private String productDescription;

}
