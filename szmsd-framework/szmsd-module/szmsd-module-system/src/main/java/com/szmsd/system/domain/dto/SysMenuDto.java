package com.szmsd.system.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单权限表 sys_menu
 * 
 * @author lzw
 */

@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "菜单权限表", description = "sys_menu")
public class SysMenuDto
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "menuId", type = IdType.AUTO)
    @ApiModelProperty(value = "菜单ID")
    private Long menuId;

    @NotNull(message = "权限类型不能为空")
    @ApiModelProperty(value = "权限类型：1-PC，2-APP,3-VIP")
    private Integer type;

    @ApiModelProperty(value = "机构网点级别code")
    @Excel(name = "机构网点级别code")
    private String siteRankCode;

    @ApiModelProperty(value = "机构网点级别名称")
    @Excel(name = "机构网点级别")
    private String siteRankName;

    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    @ApiModelProperty(value = "菜单名称")
    private String menuName;

    @ApiModelProperty(value = "父菜单名称")
    private String parentName;

    @ApiModelProperty(value = "父菜单ID")
    private Long parentId;

    @NotNull(message = "显示顺序不能为空")
    @ApiModelProperty(value = "显示顺序")
    private Integer orderNum;

    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    @ApiModelProperty(value = "路由地址")
    private String path;

    @ApiModelProperty(value = "组件名称")
    private String componentName;

    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    @ApiModelProperty(value = "组件路径")
    private String component;

    @ApiModelProperty(value = "是否为外链（0是 1否）")
    private Integer isFrame;

    @NotBlank(message = "菜单类型不能为空")
    @ApiModelProperty(value = "类型（M目录 C菜单 F按钮）")
    private String menuType;

    @ApiModelProperty(value = "显示状态（0显示 1隐藏）")
    private String visible;

    @ApiModelProperty(value = "菜单状态（0显示 1隐藏）")
    private String status;

    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    @ApiModelProperty(value = "权限字符串")
    private String perms;

    @ApiModelProperty(value = "菜单图标")
    private String icon;

//    @ApiModelProperty(value = "子菜单")
//    @TableField(exist = false)
//    private List<SysMenuDto> children = new ArrayList<SysMenuDto>();


}
