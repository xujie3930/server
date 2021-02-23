package com.szmsd.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 * 多语言配置表
 * </p>
 *
 * @author ziling
 * @since 2020-08-06
 */
@TableName("sys_lanres")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "SysLanres对象", description = "多语言配置表")
public class SysLanres {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "编号")
    private String code;

    @ApiModelProperty(value = "简体中文")
    @Excel(name = "简体中文")
    private String strid;

    @ApiModelProperty(value = "英文")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "英文")
    private String lan1;

    @ApiModelProperty(value = "泰文")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    @Excel(name = "泰文")
    private String lan2;

    @ApiModelProperty(value = "语言3")
    private String lan3;

    @ApiModelProperty(value = "语言4")
    private String lan4;

    @ApiModelProperty(value = "语言5")
    private String lan5;

    @ApiModelProperty(value = "是否可见")
    @TableField("VISIBLE")
    private Integer visible;

    @ApiModelProperty(value = "分组类型")
    @TableField("GROUPTYPE")
    private Integer grouptype;

    @ApiModelProperty(value = "创建人名称")
    private String createByName;

    @ApiModelProperty(value = "最后修改人名称")
    private String updateByName;

    @ApiModelProperty(value = "删除状态：0删除 1未删除")
    private String deletedStatus;

    @ApiModelProperty(value = "预留字段")
    private String parm1;

    @ApiModelProperty(value = "预留字段")
    private String parm2;

    @ApiModelProperty(value = "预留字段")
    private String parm3;

    @ApiModelProperty(value = "预留字段")
    private String parm4;

    @ApiModelProperty(value = "预留字段")
    private String parm5;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "app标识 0=未勾选 1=勾选")
    private String app;


}
