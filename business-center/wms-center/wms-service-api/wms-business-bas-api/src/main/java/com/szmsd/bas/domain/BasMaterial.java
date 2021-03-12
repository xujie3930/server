package com.szmsd.bas.domain;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
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
* @author l
* @since 2021-03-12
*/
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="", description="BasMaterial对象")
public class BasMaterial extends BaseEntity {

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
    private String delFlag;

    @ApiModelProperty(value = "产品名称")
    @Excel(name = "产品名称")
    private String productName;

    @ApiModelProperty(value = "产品编码")
    @Excel(name = "产品编码")
    @TableField("`code`")
    private String code;

    @ApiModelProperty(value = "初始重量g")
    @Excel(name = "初始重量g")
    private BigDecimal initWeight;

    @ApiModelProperty(value = "初始长 cm")
    @Excel(name = "初始长 cm")
    private BigDecimal initLength;

    @ApiModelProperty(value = "初始宽 cm")
    @Excel(name = "初始宽 cm")
    private BigDecimal initWidth;

    @ApiModelProperty(value = "初始高 cm")
    @Excel(name = "初始高 cm")
    private BigDecimal initHeight;

    @ApiModelProperty(value = "是否激活")
    @Excel(name = "是否激活")
    private Boolean isActive;

    @ApiModelProperty(value = "初始体积 cm3")
    @Excel(name = "初始体积 cm3")
    private BigDecimal initVolume;

    @ApiModelProperty(value = "客户（卖家）编码")
    @Excel(name = "客户（卖家）编码")
    private String sellerCode;

    @ApiModelProperty(value = "包材")
    @Excel(name = "包材")
    private String category;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String type;

    @ApiModelProperty(value = "类型名")
        @TableField("typeName")
    @Excel(name = "类型名")
    private String typeName;

    @ApiModelProperty(value = "产品说明")
    @Excel(name = "产品说明")
    private String productDescription;

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


}
