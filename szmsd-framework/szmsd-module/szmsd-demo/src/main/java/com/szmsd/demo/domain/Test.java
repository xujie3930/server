package com.szmsd.demo.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.annotation.Excel.*;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@TableName("test")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "test对象", description = "test")
public class Test {

    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键id", cellType = ColumnType.NUMERIC)
    @ApiModelProperty(value = "主键id")
    private Long id;

    @Excel(name = "名称")
    @ApiModelProperty(value = "名称")
    private String name;

    @Excel(name = "性别：0=男，1=女")
    @ApiModelProperty(value = "性别：0=男，1=女")
    private Long sex;

    @Excel(name = "笔记")
    @ApiModelProperty(value = "笔记")
    private String note;

    @Excel(name = "年龄")
    @ApiModelProperty(value = "年龄")
    @NotNull
    private Long age;


    @Excel(name = "备注")
    @ApiModelProperty(value = "备注")
    private String remark;
    @Excel(name = "状态（0正常 1停用）")
    @ApiModelProperty(value = "状态（0正常 1停用）")
    private String status;
    @Excel(name = "版本号")
    @ApiModelProperty(value = "版本号")
    private String version;

    @Excel(name = "删除标志（0代表存在 2代表删除）")
    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty(value = "创建者ID")
    private String createBy;

    @TableField(fill = FieldFill.INSERT)
    @Excel(name = "删除标志（0代表存在 2代表删除）", type = Type.EXPORT)
    @ApiModelProperty(value = "创建者")
    private String createByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新者ID")
    private String updateBy;

    @TableField(fill = FieldFill.UPDATE)
    @ApiModelProperty(value = "更新者")
    @Excel(name = "删除标志（0代表存在 2代表删除）", type = Type.EXPORT)
    private String updateByName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
