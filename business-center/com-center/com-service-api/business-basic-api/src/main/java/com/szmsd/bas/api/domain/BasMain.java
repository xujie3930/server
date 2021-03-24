package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-06-18
 */

@Data
public class BasMain{

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    private Long id;

    @ApiModelProperty(value = "主类id")
    @Excel(name = "主类id")
    private String mainCode;

    @ApiModelProperty(value = "主类名称（中）")
    @Excel(name = "主类名称（中）")
    private String mainName;

    @ApiModelProperty(value = "主类名称（英）")
    @Excel(name = "主类名称（英）")
    private String mainNameEn;

    @ApiModelProperty(value = "主类名称（阿拉伯）")
    @Excel(name = "主类名称（阿拉伯）")
    private String mainNameAr;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createId;

    @ApiModelProperty(value = "修改者id")
    @Excel(name = "修改者id")
    private String updateId;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @Excel(name = "状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除")
    @Excel(name = "删除标志（0代表存在 2代表删除")
    private String delFlag;

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

    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建者")
    @TableField(exist = false)
    private String createByName;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    /** 更新者 */
    @ApiModelProperty(value = "更新者")
    @TableField(exist = false)
    private String updateByName;
}
