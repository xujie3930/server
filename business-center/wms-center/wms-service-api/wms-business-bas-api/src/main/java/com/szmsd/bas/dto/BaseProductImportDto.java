package com.szmsd.bas.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.utils.StringUtils;
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
    @Excel(name = "SKU编码" ,type = Excel.Type.IMPORT)
    private String code;

    @ExcelProperty(index = 13)
    @ApiModelProperty(value = "重量(g)")
    @Excel(name = "重量(g)" ,type = Excel.Type.IMPORT)
    private Double initWeight;

    @ExcelProperty(index = 14)
    @ApiModelProperty(value = "长（cm）")
    @Excel(name = "长（cm）" ,type = Excel.Type.IMPORT)
    private Double initLength;

    @ExcelProperty(index = 15)
    @ApiModelProperty(value = "宽（cm）")
    @Excel(name = "宽（cm）" ,type = Excel.Type.IMPORT)
    private Double initWidth;

    @ExcelProperty(index = 16)
    @ApiModelProperty(value = "高（cm）")
    @Excel(name = "高（cm）" ,type = Excel.Type.IMPORT)
    private Double initHeight;

    @ExcelProperty(index = 17)
    @ApiModelProperty(value = "体积（cm3）")
    @Excel(name = "体积（cm3）" ,type = Excel.Type.IMPORT)
    private Double initVolume;


    @ExcelProperty(index = 18)
    @ApiModelProperty(value = "自定义sku编码")
    @Excel(name = "自定义sku编码" ,type = Excel.Type.IMPORT)
    private Double customizeSkuCode;

    @ExcelProperty(index = 19)
    @ApiModelProperty(value = "产品介绍地址")
    @Excel(name = "产品介绍地址" ,type = Excel.Type.IMPORT)
    private Double productIntroductAddress;

    @ExcelProperty(index = 20)
    @ApiModelProperty(value = "材质")
    @Excel(name = "材质" ,type = Excel.Type.IMPORT)
    private Double materialQuality;

    @ExcelProperty(index = 21)
    @ApiModelProperty(value = "用途")
    @Excel(name = "用途" ,type = Excel.Type.IMPORT)
    private Double purpose;

    @ExcelProperty(index = 22)
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注" ,type = Excel.Type.IMPORT)
    private Double remark;

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
    @ApiModelProperty(value = "带电信息")
    @Excel(name = "带电信息" ,type = Excel.Type.IMPORT)
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

    @ExcelProperty(index = 12)
    @ApiModelProperty(value = "产品说明")
    @Excel(name = "产品说明" ,type = Excel.Type.IMPORT)
    private String productDescription;

    @ExcelProperty(index = 7)
    @ApiModelProperty(value = "是否附带包材")
    @Excel(name = "是否附带包材" ,type = Excel.Type.IMPORT)
    private String havePackingMaterialName;

    @ApiModelProperty(value = "是否自备包材")
    private Boolean havePackingMaterial;

    @ExcelProperty(index = 10)
    @Excel(name = "包装材料价格区间" ,type = Excel.Type.IMPORT)
    @ApiModelProperty(value = "价格区间")
    private String priceRange;


    @ExcelProperty(index = 11)
//    @ApiModelProperty(value = "是否一票多件)")
//    private String multipleTicketFlagStr;
    @ApiModelProperty(value = "海关编码")
    private String hsCode;

    /*public void setMultipleTicketFlagStr(String multipleTicketFlagStr) {
        this.multipleTicketFlagStr = multipleTicketFlagStr;
        if (StringUtils.isNotBlank(multipleTicketFlagStr) && "是".equals(multipleTicketFlagStr)) {
            this.multipleTicketFlag = 1;
        } else {
            this.multipleTicketFlag = 0;
        }
    }

    @ApiModelProperty(value = "是否一票多件)")
    private Integer multipleTicketFlag;*/



}
