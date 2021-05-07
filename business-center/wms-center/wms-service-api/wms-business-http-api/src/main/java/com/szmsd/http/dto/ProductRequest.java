package com.szmsd.http.dto;

import com.szmsd.common.core.annotation.Excel;
import com.szmsd.http.annotation.LogIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@LogIgnore({"productImage"})
public class ProductRequest {
    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "产品编码")
    private String code;
    @ApiModelProperty(value = "初始重量g")
    @Excel(name = "初始重量g")
    private Double initWeight;

    @ApiModelProperty(value = "初始长 cm")
    @Excel(name = "初始长 cm")
    private Double initLength;

    @ApiModelProperty(value = "初始宽 cm")
    @Excel(name = "初始宽 cm")
    private Double initWidth;

    @ApiModelProperty(value = "初始高 cm")
    @Excel(name = "初始高 cm")
    private Double initHeight;

    @ApiModelProperty(value = "是否激活")
    @Excel(name = "是否激活")
    private Boolean isActive;

    @ApiModelProperty(value = "产品文件格式 jpg / png / jpeg")
    @Excel(name = "产品文件格式 jpg / png / jpeg")
    private String suffix;

    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "sku/包材")
    @Excel(name = "sku/包材")
    private String category;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "物流包装要求姓名")
    @Excel(name = "物流包装要求姓名")
    private String suggestPackingMaterial;

    @ApiModelProperty(value = "绑定专属包材产品编码")
    @Excel(name = "绑定专属包材产品编码")
    private String bindCode;

    @ApiModelProperty(value = "产品图片")
    @Excel(name = "产品图片")
    private String productImage;
}
