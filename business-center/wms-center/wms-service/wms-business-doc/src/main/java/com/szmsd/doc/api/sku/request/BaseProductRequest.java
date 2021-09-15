package com.szmsd.doc.api.sku.request;

import com.szmsd.common.core.annotation.Excel;
import com.szmsd.doc.api.SwaggerDictionary;
import com.szmsd.putinstorage.enums.SourceTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;
import java.math.BigDecimal;


/**
 * <p>
 *
 * </p>
 *
 * @author jr
 * @since 2021-03-04
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "BaseProduct产品(sku/包材)对象 调用主子类别 /getSubName?code=061,062,060,059")
public class BaseProductRequest {

    private static final long serialVersionUID = 1L;

    //    @ApiModelProperty(value = "产品名称", required = true)
//    @Size(max = 255)
//    @NotBlank(message = "产品名称不能为空")
//    private String productName;
//
//    @ApiModelProperty(value = "产品编码", required = true)
//    @Size(max = 50)
//    @NotBlank(message = "产品编码不能为空")
//    private String code;
    @DecimalMin(value = "0.01", message = "初始重量异常")
    @ApiModelProperty(value = "初始重量g",example = "1",required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始重量不能为空")
    private Double initWeight;
    @DecimalMin(value = "0.01", message = "初始长度异常")
    @ApiModelProperty(value = "初始长 cm",example = "1", required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始长度不能为空")
    private Double initLength;
    @DecimalMin(value = "0.01", message = "初始宽度异常")
    @ApiModelProperty(value = "初始宽 cm",example = "1", required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始宽度不能为空")
    private Double initWidth;
    @DecimalMin(value = "0.01", message = "初始高度异常")
    @ApiModelProperty(value = "初始高 cm",example = "1", required = true)
    @Digits(integer = 8, fraction = 2)
    @NotNull(message = "初始高度不能为空")
    private Double initHeight;

    @ApiModelProperty(value = "是否激活 默认true", hidden = true)
    private Boolean isActive = true;

    @ApiModelProperty(value = "产品图片")
    @Size(max = 255)
    private String productImage;

    @ApiModelProperty(value = "产品文件格式 jpg / png / jpeg")
    @Size(max = 40)
    private String suffix;
    @DecimalMin(value = "0.01", message = "初始体积异常")
    @ApiModelProperty(value = "初始体积 cm3",example = "1", required = true)
    @Digits(integer = 14, fraction = 2)
    @NotNull(message = "初始体积不能为空")
    private BigDecimal initVolume;

    @NotBlank(message = "客户（卖家）编码不能为空")
    @ApiModelProperty(value = "客户（卖家）编码", example = "CNYWO7", required = true)
    @Excel(name = "客户（卖家）编码")
    @Size(max = 100)
    private String sellerCode;

    @ApiModelProperty(value = "中文申报品名",example = "中文申报品名",required = true)
    @Excel(name = "中文申报品名")
    @Size(max = 200)
    @NotBlank(message = "中文申报品名不能为空")
    private String productNameChinese;

    @ApiModelProperty(value = "申报价值", required = true)
    @Excel(name = "申报价值")
    @NotNull(message = "申报价值不能为空")
    @Digits(integer = 8, fraction = 2)
    private Double declaredValue;
    @SwaggerDictionary(dicCode = "059",dicKey = "subValue")
    @ApiModelProperty(value = "产品属性编号", example = "GeneralCargo", required = true)
    @Size(max = 50)
    @NotBlank(message = "产品属性编号不能为空")
    private String productAttribute;

    @ApiModelProperty(value = "产品属性名 [普货:GeneralCargo,带电:Battery,液体:Liquid,粉末:Powder,带磁:MagneticBand]", allowableValues = "[普货,电池,液体,粉末,带磁]", example = "普货", required = true, hidden = true)
    @Excel(name = "产品属性名")
//    @NotBlank(message = "产品属性名不能为空")
    @Size(max = 100)
    private String productAttributeName;
    @SwaggerDictionary(dicCode = "060",dicKey = "subValue")
    @ApiModelProperty(value = "带电信息编号 主子类别(060 / ddxx -#{subValue})", example = "drycell")
    @Size(max = 50)
    private String electrifiedMode;
    @SwaggerDictionary(dicCode = "060", dicKey = "subName")
    @ApiModelProperty(value = "带电信息名 主子类别(060 / ddxx -#{subName})", example = "干电池",hidden = true)
    @Excel(name = "带电信息名")
    @Size(max = 100)
    private String electrifiedModeName;
    @SwaggerDictionary(dicCode = "061",dicKey = "subValue")
    @ApiModelProperty(value = "电池包装编号 主子类别(061 / dcbb -#{subValue})", example = "built_in")
    @Size(max = 50)
    private String batteryPackaging;
    @SwaggerDictionary(dicCode = "061", dicKey = "subName")
    @ApiModelProperty(value = "电池包装名 主子类别(061 / dcbb -#{subName})", example = "内置", hidden = true)
    @Excel(name = "电池包装名")
    @Size(max = 100)
    private String batteryPackagingName;

    @ApiModelProperty(value = "是否附带包材", allowableValues = "true,false", example = "false")
    private Boolean havePackingMaterial;

    @ApiModelProperty(value = "附带包材 产品编码 /product/listSku", example = "SCN72000081")
    @Size(max = 100)
    private String bindCode;

    @ApiModelProperty(value = "附带包材 产品名 /api/sku/list params:{category：包材,sellerCode: CNYWO7}", notes = "category: 包材 sellerCode: CNYWO7")
    @Excel(name = "绑定专属包材产品名")
    @Size(max = 100)
    private String bindCodeName;

    @ApiModelProperty(value = "物流包装要求 /api/sku/list params:{category：包材,sellerCode: CNYWO7}", example = "编织袋",hidden = true)
    @Excel(name = "物流包装要求")
    @Size(max = 50)
    private String suggestPackingMaterial;

    @ApiModelProperty(value = "物流包装要求编码 packing/listPacking -#{packageMaterialCode}", example = "B001")
    @Size(max = 100)
    private String suggestPackingMaterialCode;

    @ApiModelProperty(value = "价格区间(可修改) packing/listPacking -#{priceRange}", example = "10.00-10.00")
    @Excel(name = "价格区间")
    @Size(max = 255)
    private String priceRange;

    @ApiModelProperty(value = "产品说明", example = "产品说明", required = true)
    @Excel(name = "产品说明")
    @Size(max = 1000)
    @NotBlank(message = "产品说明不能为空")
    private String productDescription;

    @ApiModelProperty(value = "产品介绍地址", example = "产品介绍地址")
    @Size(max = 1000)
    private String productIntroductAddress;

    @ApiModelProperty(value = "类别", allowableValues = "sku", hidden = true)
    @Size(max = 20)
    private String category = "SKU";

    @ApiModelProperty(value = "类别编码", hidden = true)
    @Size(max = 100)
    private String categoryCode;

//    @ApiModelProperty(value = "是否仓库验收")
//    private Boolean warehouseAcceptance;

//    @ApiModelProperty(value = "属性1")
//    @Size(max = 200)
//    @JsonIgnore
//    private String attribute1;
//
//    @ApiModelProperty(value = "属性2")
//    @Size(max = 200)
//    @JsonIgnore
//    private String attribute2;
//
//    @ApiModelProperty(value = "属性3")
//    @Size(max = 200)
//    @JsonIgnore
//    private String attribute3;
//
//    @ApiModelProperty(value = "属性4")
//    @Size(max = 200)
//    @JsonIgnore
//    private String attribute4;
//
//    @ApiModelProperty(value = "属性5")
//    @Size(max = 200)
//    private String attribute5;

//    @ApiModelProperty(value = "仓库测量重量g")
//    @NotBlank(message = "仓库测量重量不能为空")
//    @Digits(integer = 8,fraction = 2)
//    private Double weight;
//
//    @ApiModelProperty(value = "仓库测量长 cm")
//    @NotBlank(message = "仓库测量长度不能为空")
//    @Digits(integer = 8,fraction = 2)
//    private Double length;
//
//    @ApiModelProperty(value = "仓库测量宽 cm")
//    @NotBlank(message = "仓库测量宽度不能为空")
//    @Digits(integer = 8,fraction = 2)
//    private Double width;
//
//    @ApiModelProperty(value = "仓库测量高 cm")
//    @NotBlank(message = "仓库测量高度不能为空")
//    @Digits(integer = 8,fraction = 2)
//    private Double height;
//
//    @ApiModelProperty(value = "仓库测量体积 cm3")
//    @Digits(integer = 14,fraction = 2)
//    @NotBlank(message = "仓库测量体积不能为空")
//    private BigDecimal volume;

//    @ApiModelProperty(value = "操作员")
//    @Excel(name = "操作员")
//    @Size(max = 100)
//    private String operator;
//
//    @ApiModelProperty(value = "操作时间")
//    private Date operateOn;

    @ApiModelProperty(value = "仓库编码", example = "NJ", required = true)
    @Size(max = 50, message = "仓库编码不能大于50字符")
    private String warehouseCode;

    @ApiModelProperty(value = "关联单号")
    @Size(max = 100)
    private String orderNo;

//    @ApiModelProperty(value = "来源")
//    @Size(max = 50)
//    private String source;

    @ApiModelProperty(value = "海关编码")
    @Size(max = 200)
    private String hsCode;
    @ApiModelProperty(value = "来源", hidden = true)
    private String source = SourceTypeEnum.DOC.name();
}
