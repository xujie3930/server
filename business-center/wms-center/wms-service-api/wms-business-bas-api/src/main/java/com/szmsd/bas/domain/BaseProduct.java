package com.szmsd.bas.domain;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
* <p>
    * 
    * </p>
*
* @author jr
* @since 2021-03-04
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="BaseProduct产品(sku/包材)对象")
public class BaseProduct extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "id")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "删除标识：0未删除 1已删除")
    @Excel(name = "删除标识：0未删除 1已删除")
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    @TableLogic(value = "0", delval = "1")
    private String delFlag;

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

    @ApiModelProperty(value = "产品图片")
    @Excel(name = "产品图片")
    private String productImage;

    @ApiModelProperty(value = "产品文件格式 jpg / png / jpeg")
    @Excel(name = "产品文件格式 jpg / png / jpeg")
    private String suffix;

    @ApiModelProperty(value = "初始体积 cm3")
    @Excel(name = "初始体积 cm3")
    private BigDecimal initVolume;

    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "中文申报品名")
    @Excel(name = "中文申报品名")
    private String productNameChinese;

    @ApiModelProperty(value = "申报价值")
    @Excel(name = "申报价值")
    private String declaredValue;

    @ApiModelProperty(value = "产品属性编号")
    @Excel(name = "产品属性编号")
    private String productAttribute;

    @ApiModelProperty(value = "产品属性名")
    @Excel(name = "产品属性名")
    private String productAttributeName;

    @ApiModelProperty(value = "带电信息编号")
    @Excel(name = "带电信息编号")
    private String electrifiedMode;

    @ApiModelProperty(value = "带电信息名")
    @Excel(name = "带电信息名")
    private String electrifiedModeName;

    @ApiModelProperty(value = "电池包装编号")
    @Excel(name = "电池包装编号")
    private String batteryPackaging;

    @ApiModelProperty(value = "电池包装名")
    @Excel(name = "电池包装名")
    private String batteryPackagingName;

    @ApiModelProperty(value = "是否附带包材")
    @Excel(name = "是否附带包材")
    private Boolean havePackingMaterial;

    @ApiModelProperty(value = "绑定专属包材产品编码")
    @Excel(name = "绑定专属包材产品编码")
    private String bindCode;

    @ApiModelProperty(value = "绑定专属包材产品名")
    @Excel(name = "绑定专属包材产品名")
    private String bindCodeName;

    @ApiModelProperty(value = "物流包装要求编码")
    @Excel(name = "物流包装要求编码")
    private String suggestPackingMaterial;

    @ApiModelProperty(value = "物流包装要求姓名")
    @Excel(name = "物流包装要求姓名")
    private String suggestPackingMaterialName;

    @ApiModelProperty(value = "开始价格")
    @Excel(name = "开始价格")
    private BigDecimal beginPrice;

    @ApiModelProperty(value = "结束价格")
    @Excel(name = "结束价格")
    private BigDecimal endPrice;

    @ApiModelProperty(value = "产品说明")
    @Excel(name = "产品说明")
    private String productDescription;

    @ApiModelProperty(value = "产品介绍地址")
    @Excel(name = "产品介绍地址")
    private String productIntroductAddress;

    @ApiModelProperty(value = "sku/包材")
    @Excel(name = "sku/包材")
    private String category;

    @ApiModelProperty(value = "是否仓库验收")
    @Excel(name = "是否仓库验收")
    private Boolean warehouseAcceptance;

    @ApiModelProperty(value = "属性1")
    @Excel(name = "属性1")
    private String attribute1;

    @ApiModelProperty(value = "属性2")
    @Excel(name = "属性2")
    private String attribute2;

    @ApiModelProperty(value = "属性3")
    @Excel(name = "属性3")
    private String attribute3;

    @ApiModelProperty(value = "属性4")
    @Excel(name = "属性4")
    private String attribute4;

    @ApiModelProperty(value = "属性5")
    @Excel(name = "属性5")
    private String attribute5;

    @ApiModelProperty(value = "仓库测量重量g")
    @Excel(name = "仓库测量重量g")
    private Double Weight;

    @ApiModelProperty(value = "仓库测量长 cm")
    @Excel(name = "仓库测量长 cm")
    private Double Length;

    @ApiModelProperty(value = "仓库测量宽 cm")
    @Excel(name = "仓库测量宽 cm")
    private Double Width;

    @ApiModelProperty(value = "仓库测量高 cm")
    @Excel(name = "仓库测量高 cm")
    private Double Height;

    @ApiModelProperty(value = "仓库测量体积 cm3")
    @Excel(name = "仓库测量体积 cm3")
    private BigDecimal Volume;

    @ApiModelProperty(value = "操作员")
    @Excel(name = "操作员")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    @Excel(name = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "仓库编码")
    @Excel(name = "仓库编码")
    private String warehouseCode;

    @ApiModelProperty(value = "业务主键，用来做幂等校验")
    @Excel(name = "业务主键，用来做幂等校验")
    private String transactionId;

    @ApiModelProperty(value = "关联单号")
    @Excel(name = "关联单号")
    private String orderNo;

}
