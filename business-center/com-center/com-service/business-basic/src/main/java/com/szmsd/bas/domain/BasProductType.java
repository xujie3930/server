package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.CodeToNameElement;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.enums.CodeToNameEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-06-13
 */
@TableName("bas_product_type")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasProductType对象", description = "")
public class BasProductType {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "产品类型编号")
    @Excel(name = "产品类型编号")
    private String productTypeCode;

    @ApiModelProperty(value = "产品名称（中）")
    @Excel(name = "产品名称（中）")
    @CodeToNameElement(type = CodeToNameEnum.BAS_PRODUCT, keyCode = "productTypeCode")
    private String productTypeName;

    @ApiModelProperty(value = "产品名称（英）")
    @Excel(name = "产品名称（英）")
    private String productTypeNameEn;

    @ApiModelProperty(value = "产品名称（阿拉伯）")
    @Excel(name = "产品名称（阿拉伯）")
    private String productTypeNameAr;

    @ApiModelProperty(value = "单号前缀")
    @Excel(name = "单号前缀")
    private String prefixNumber;

    @ApiModelProperty(value = "创建者id")
    @Excel(name = "创建者id")
    private String createId;

    @ApiModelProperty(value = "修改者ID")
    @Excel(name = "修改者ID")
    private String updateId;

    @ApiModelProperty(value = "预留字段1")
    @Excel(name = "预留字段1")
    private String parm1;

    @ApiModelProperty(value = "预留字段2")
    @Excel(name = "预留字段2")
    private String parm2;

    @ApiModelProperty(value = "预留字段3")
    @Excel(name = "预留字段3")
    private String parm3;

    @ApiModelProperty(value = "预留字段4")
    @Excel(name = "预留字段4")
    private String parm4;

    @ApiModelProperty(value = "预留字段5")
    @Excel(name = "预留字段5")
    private String parm5;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private String version;

    @ApiModelProperty(value = "录入时间")
    @Excel(name = "录入时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;
}
