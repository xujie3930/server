package com.szmsd.doc.api.sku.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.bind.DefaultValue;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Date;


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
@ApiModel(value="", description="BaseProduct产品(sku/包材)对象")
public class BaseProductRequest {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "产品名称", required = true)
    @Size(max = 255)
    @NotBlank(message = "产品名称不能为空")
    private String productName;

    @ApiModelProperty(value = "产品编码", required = true)
    @Size(max = 50)
    @NotBlank(message = "产品编码不能为空")
    private String code;

    @ApiModelProperty(value = "初始重量g", required = true)
    @Digits(integer = 8,fraction = 2)
    @NotNull(message = "初始重量不能为空")
    private Double initWeight;

    @ApiModelProperty(value = "初始长 cm", required = true)
    @Digits(integer = 8,fraction = 2)
    @NotNull(message = "初始长度不能为空")
    private Double initLength;

    @ApiModelProperty(value = "初始宽 cm", required = true)
    @Digits(integer = 8,fraction = 2)
    @NotNull(message = "初始宽度不能为空")
    private Double initWidth;

    @ApiModelProperty(value = "初始高 cm", required = true)
    @Digits(integer = 8,fraction = 2)
    @NotNull(message = "初始高度不能为空")
    private Double initHeight;

    @ApiModelProperty(value = "是否激活 默认true")
    private Boolean isActive = true;

    @ApiModelProperty(value = "产品图片")
    @Size(max = 255)
    private String productImage;

    @ApiModelProperty(value = "产品文件格式 jpg / png / jpeg")
    @Size(max = 40)
    private String suffix;

    @ApiModelProperty(value = "初始体积 cm3", required = true)
    @Digits(integer = 14,fraction = 2)
    @NotNull(message = "初始体积不能为空")
    private BigDecimal initVolume;

    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    @Size(max = 100)
    private String sellerCode;

    @ApiModelProperty(value = "中文申报品名", required = true)
    @Excel(name = "中文申报品名")
    @Size(max = 200)
    @NotBlank(message = "中文申报品名不能为空")
    private String productNameChinese;

    @ApiModelProperty(value = "申报价值", required = true)
    @Excel(name = "申报价值")
    @NotNull(message = "申报价值不能为空")
    @Digits(integer = 8,fraction = 2)
    private Double declaredValue;

    @ApiModelProperty(value = "产品属性编号", required = true)
    @Size(max = 50)
    @NotBlank(message = "产品属性编号不能为空")
    private String productAttribute;

    @ApiModelProperty(value = "产品属性名", required = true)
    @Excel(name = "产品属性名")
    @NotBlank(message = "产品属性名不能为空")
    @Size(max = 100)
    private String productAttributeName;

    @ApiModelProperty(value = "带电信息编号")
    @Size(max = 50)
    private String electrifiedMode;

    @ApiModelProperty(value = "带电信息名")
    @Excel(name = "带电信息名")
    @Size(max = 100)
    private String electrifiedModeName;

    @ApiModelProperty(value = "电池包装编号")
    @Size(max = 50)
    private String batteryPackaging;

    @ApiModelProperty(value = "电池包装名")
    @Excel(name = "电池包装名")
    @Size(max = 100)
    private String batteryPackagingName;

    @ApiModelProperty(value = "是否附带包材")
    private Boolean havePackingMaterial;

    @ApiModelProperty(value = "绑定专属包材产品编码")
    @Size(max = 100)
    private String bindCode;

    @ApiModelProperty(value = "绑定专属包材产品名")
    @Excel(name = "绑定专属包材产品名")
    @Size(max = 100)
    private String bindCodeName;

    @ApiModelProperty(value = "物流包装要求姓名")
    @Excel(name = "物流包装要求姓名")
    @Size(max = 50)
    private String suggestPackingMaterial;

    @ApiModelProperty(value = "物流包装要求编码")
    @Size(max = 100)
    private String suggestPackingMaterialCode;

//    @ApiModelProperty(value = "价格区间")
//    @Excel(name = "价格区间")
//    @Size(max = 255)
//    private String priceRange;

    @ApiModelProperty(value = "产品说明", required = true)
    @Excel(name = "产品说明")
    @Size(max = 1000)
    @NotBlank(message = "产品说明不能为空")
    private String productDescription;

    @ApiModelProperty(value = "产品介绍地址")
    @Size(max = 1000)
    private String productIntroductAddress;

    @ApiModelProperty(value = "类别")
    @Size(max = 20)
    private String category;

    @ApiModelProperty(value = "类别编码")
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

    @ApiModelProperty(value = "操作员")
    @Excel(name = "操作员")
    @Size(max = 100)
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "仓库编码")
    @Size(max = 50)
    private String warehouseCode;

    @ApiModelProperty(value = "关联单号")
    @Size(max = 100)
    private String orderNo;

    @ApiModelProperty(value = "来源")
    @Size(max = 50)
    private String source;

    @ApiModelProperty(value = "海关编码")
    @Size(max = 200)
    private String hsCode;
}
