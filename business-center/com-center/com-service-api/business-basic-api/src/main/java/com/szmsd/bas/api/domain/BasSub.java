package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
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
 * @since 2020-06-18
 */

@Data
@Accessors(chain = true)
public class BasSub {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "子类id")
    @Excel(name = "子类id")
    private String subCode;



    @ApiModelProperty(value = "主类id")
    @Excel(name = "主类id")
    private String mainCode;

    @ApiModelProperty(value = "主类名称")
    @Excel(name = "主类名称")
    private String mainName;

    @ApiModelProperty(value = "子类名称（中）")
    @Excel(name = "子类名称（中）")
    private String subName;

    @ApiModelProperty(value = "子类名称（英）")
    @Excel(name = "子类名称（英）")
    private String subNameEn;

    @ApiModelProperty(value = "子类名称（阿拉伯）")
    @Excel(name = "子类名称（阿拉伯）")
    private String subNameAr;

    @ApiModelProperty(value = "录入人id")
    @Excel(name = "录入人id")
    private String createId;

    @ApiModelProperty(value = "录入人名称")
    @Excel(name = "录入人名称")
    private String createName;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人id")
    private String updateId;

    @ApiModelProperty(value = "修改人名称")
    @Excel(name = "修改人名称")
    private String updateName;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @Excel(name = "状态（0正常 1停用）")
    @TableField(exist = false)
    private String status;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @Excel(name = "删除标志（0代表存在 2代表删除）")
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

    @ApiModelProperty(value = "字典排序")
    @Excel(name = "字典排序")
    private int sort;

    @ApiModelProperty(value = "子类别值")
    @Excel(name = "子类别值")
    private String subValue;


    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    @TableField(exist = false)
    private String createBy;

    @ApiModelProperty(value = "创建者")
    @TableField(exist = false)
    private String createByName;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    @TableField(exist = false)
    private String updateBy;

    /** 更新者 */
    @ApiModelProperty(value = "更新者")
    @TableField(exist = false)
    private String updateByName;
}
