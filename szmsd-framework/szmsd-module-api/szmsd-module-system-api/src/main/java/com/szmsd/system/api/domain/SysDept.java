package com.szmsd.system.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 部门表
 * </p>
 *
 * @author lzw
 * @since 2020-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_dept")
@ApiModel(value = "机构部门表", description = "SysDept对象")
public class SysDept extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "部门id")
    @TableId(value = "id", type = IdType.UUID)
    @Excel(name = "部门id")
    private Long id;

    @ApiModelProperty(value = "代码")
    @Excel(name = "代码")
    private String deptCode;

    @ApiModelProperty(value = "父部门id")
    @Excel(name = "父部门id")
    private Long parentId;

    @ApiModelProperty(value = "祖级列表")
    @Excel(name = "祖级列表")
    private String ancestors;

    @ApiModelProperty(value = "部门名称")
    @Excel(name = "部门名称")
    private String deptName;

    @ApiModelProperty(value = "显示顺序")
    @Excel(name = "显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "所属网点")
    @Excel(name = "所属网点")
    private String siteCode;

    @ApiModelProperty(value = "负责人")
    @Excel(name = "负责人")
    private String leader;

    @ApiModelProperty(value = "手机号")
    @Excel(name = "手机号")
    private String mobile;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String phone;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;

    @ApiModelProperty(value = "地址")
    @Excel(name = "地址")
    private String address;

    @ApiModelProperty(value = "部门职能")
    @Excel(name = "部门职能")
    private String deptFunction;

    @ApiModelProperty(value = "最大编制人数")
    @Excel(name = "最大编制人数")
    private Integer peopleMaxNum;

    @ApiModelProperty(value = "最低编制")
    @Excel(name = "最低编制")
    private Integer peopleMinNum;

    @ApiModelProperty(value = "部门状态（0正常 1停用）")
    @Excel(name = "部门状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @Excel(name = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "创建ID")
    @Excel(name = "创建ID")
    private String createBy;

    @ApiModelProperty(value = "修改者id")
    @Excel(name = "修改者id")
    private String updateBy;

    @ApiModelProperty(value = "子网点列表")
    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<SysDept>();
}
