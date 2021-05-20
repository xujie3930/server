package com.szmsd.system.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.CodeToNameElement;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.annotation.Excel.ColumnType;
import com.szmsd.common.core.enums.CodeToNameEnum;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 角色表 sys_role
 *
 * @author szmsd
 */
@TableName("sys_role")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "角色表", description = "sys_role")
public class SysRole extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "roleId", type = IdType.AUTO)
    @ApiModelProperty(value = "角色ID")
    @Excel(name = "角色序号", cellType = ColumnType.NUMERIC)
    private Long roleId;

    @ApiModelProperty(value = "角色名称")
    @Excel(name = "角色名称")
    private String roleName;

    @ApiModelProperty(value = "角色类型：1-PC，2-APP,3-VIP")
    @Excel(name = "角色类型")
    private Integer type;

    @ApiModelProperty(value = "角色类型：1-PC，2-APP,3-VIP")
    @Excel(name = "角色类型")
    private String roleType;

    @ApiModelProperty(value = "网点机构级别Code")
    @Excel(name = "网点机构级别Code")
    private String siteRankCode;

    @ApiModelProperty(value = "网点机构级别：总部，中心，网点，代理")
    @Excel(name = "网点机构级别")
    @CodeToNameElement(type = CodeToNameEnum.BAS_SUB,keyCode = "siteRankCode")
    private String siteRankName;

    @ApiModelProperty(value = "角色权限标识")
    @Excel(name = "角色权限标识")
    private String roleKey;

    @Excel(name = "角色排序")
    @ApiModelProperty(value = "角色排序")
    private String roleSort;

    @ApiModelProperty(value = "数据范围（1：所有数据权限；2：自定义数据权限；3：本部门数据权限；4：本部门及以下数据权限）")
    @Excel(name = "数据范围", readConverterExp = "1=所有数据权限,2=自定义数据权限,3=本部门数据权限,4=本部门及以下数据权限")
    private String dataScope;

    @ApiModelProperty(value = "角色状态（0正常 1停用）")
    @Excel(name = "角色状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

//    @ApiModelProperty(value = "用户是否存在此角色标识 默认不存在")
//    private boolean flag = false;

    @ApiModelProperty(value = "菜单组")
    private Long[] menuIds;

//    @ApiModelProperty(value = "部门组（数据权限）")
//    private Long[] deptIds;


    public SysRole() {

    }

    public SysRole(Long roleId) {
        this.roleId = roleId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }


    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

    @NotBlank(message = "角色名称不能为空")
    @Size(min = 0, max = 30, message = "角色名称长度不能超过30个字符")
    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    @NotNull(message = "角色类型不能为空")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
//    @NotBlank(message = "网点机构级别Code不能为空")
    public String getSiteRankCode() {
        return siteRankCode;
    }

    public void setSiteRankCode(String siteRankCode) {
        this.siteRankCode = siteRankCode;
    }

//    @NotBlank(message = "网点机构级别不能为空")
    public String getSiteRankName() {
        return siteRankName;
    }

    public void setSiteRankName(String siteRankName) {
        this.siteRankName = siteRankName;
    }





//    @NotBlank(message = "权限字符不能为空")
//    @Size(min = 0, max = 100, message = "权限字符长度不能超过100个字符")
    public String getRoleKey() {
        return roleKey;
    }

    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

//    @NotBlank(message = "显示顺序不能为空")
    public String getRoleSort() {
        return roleSort;
    }

    public void setRoleSort(String roleSort) {
        this.roleSort = roleSort;
    }

    public String getDataScope() {
        return dataScope;
    }

    public void setDataScope(String dataScope) {
        this.dataScope = dataScope;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

//    public boolean isFlag()
//    {
//        return flag;
//    }
//
//    public void setFlag(boolean flag)
//    {
//        this.flag = flag;
//    }

    public Long[] getMenuIds() {
        return menuIds;
    }

    public void setMenuIds(Long[] menuIds) {
        this.menuIds = menuIds;
    }
//
//    public Long[] getDeptIds()
//    {
//        return deptIds;
//    }
//
//    public void setDeptIds(Long[] deptIds)
//    {
//        this.deptIds = deptIds;
//    }


}
