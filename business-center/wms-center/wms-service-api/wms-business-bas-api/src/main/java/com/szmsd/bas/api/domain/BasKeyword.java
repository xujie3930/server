package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
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
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "Baskeyword对象", description = "")
public class BasKeyword {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "关键字编号")
    @Excel(name = "关键字编号")
    private String keywordCode;

    @ApiModelProperty(value = "关键字名称")
    @Excel(name = "关键字名称")
    private String keywordName;

    @ApiModelProperty(value = "目的地code")
    @Excel(name = "目的地code")
    private String siteCode;

    @ApiModelProperty(value = "网点名称")
    @Excel(name = "网点名称")
    private String siteName;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String keywordRemark;

    @ApiModelProperty(value = "录入人id")
    @Excel(name = "录入人id")
    private String createId;

    @ApiModelProperty(value = "录入人id")
    @Excel(name = "录入人id")
    private String createBy;

    @ApiModelProperty(value = "录入网点")
    @Excel(name = "录入网点")
    private String createSite;

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

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "录入时间")
    @Excel(name = "录入时间")
    private Date createTime;

    @ApiModelProperty(value = "目的地营业件网点code")
    @Excel(name = "营业网点code")
    private String businesSiteCode;

    @ApiModelProperty(value = "目的地营业件网点")
    @Excel(name = "营业网点")
    private String businesSite;

    @ApiModelProperty(value = "城市id")
    @Excel(name = "城市id")
    private String cityCode;

    @ApiModelProperty(value = "城市名")
    @Excel(name = "城市名")
    private String cityName;

    @ApiModelProperty(value = "分拣码")
    @Excel(name = "分拣码")
    private String sortingCode;
 }
