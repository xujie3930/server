package com.szmsd.system.api.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.annotation.Excel.ColumnType;
import com.szmsd.common.core.annotation.Excel.Type;
import com.szmsd.common.core.annotation.Excels;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

/**
 * 用户对象 sys_user
 *
 * @author szmsd
 */

@TableName("sys_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "用户对象", description = "sys_user")
public class SysUser extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id")
    @Excel(name = "用户序号", cellType = ColumnType.NUMERIC, prompt = "用户编号")
    private Long userId;

    @ApiModelProperty(value = "网点编号")
    @Excel(name = "网点编号", type = Type.IMPORT)
    private String siteCode;

    @ApiModelProperty(value = "网点名称")
    @Excel(name = "网点名称", type = Type.IMPORT)
    private String siteName;

    @ApiModelProperty(value = "用户账号")
    @Excel(name = "登录名称")
    private String userName;


    @Excel(name = "用户名称")
    @ApiModelProperty(value = "用户名称")
    private String nickName;

    @Excel(name = "用户类型", readConverterExp = "00=系统用户,01=VIP用户")
    @ApiModelProperty(value = "用户类型（00-系统用户 01-VIP用户")
    private String userType;


    @ApiModelProperty(value = "微信用户唯一标识")
    private String openid;

    @Excel(name = "用户邮箱")
    @ApiModelProperty(value = "用户邮箱")
    private String email;

    @Excel(name = "手机号码")
    @ApiModelProperty(value = "手机号码")
    private String phonenumber;

    @Excel(name = "用户性别", readConverterExp = "0=男,1=女,2=未知")
    @ApiModelProperty(value = "用户性别")
    private String sex;

    @ApiModelProperty(value = "用户头像")
    private String avatar;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "服务产品")
    @Excel(name = "服务产品")
    private String serviceProduct;

    @ApiModelProperty(value = "巴枪使用密码")
    @Excel(name = "巴枪使用密码")
    private String spearPassword;

    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "最后登陆IP")
    @Excel(name = "最后登陆IP", type = Type.EXPORT)
    private String loginIp;

    @ApiModelProperty(value = "最后登陆时间")
    @Excel(name = "最后登陆时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Type.EXPORT)
    private Date loginDate;

    @Excels({
            @Excel(name = "网点名称", targetAttr = "deptName", type = Type.EXPORT),
            @Excel(name = "负责人", targetAttr = "leader", type = Type.EXPORT)
    })
    @ApiModelProperty(value = "网点对象")
    @TableField(exist = false)
    private SysSite site;

    @ApiModelProperty(value = "角色对象")
    @TableField(exist = false)
    private List<SysRole> roles;

    @ApiModelProperty(value = "角色组")
    @TableField(exist = false)
    private Long[] roleIds;

//    @ApiModelProperty(value = "岗位组")
//    @TableField(exist = false)
//    private Long[] postIds;

    /** 创建者 */
    @ApiModelProperty(value = "创建者")
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 更新者 */
    @ApiModelProperty(value = "更新者")
    @TableField(fill = FieldFill.UPDATE)
    private String updateBy;

    @ApiModelProperty(value = "客户代码")
    @Excel(name = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "判断角色里面有没有全部数据权限的")
    @TableField(exist = false)
    private boolean allDataScope;

    @ApiModelProperty(value = "允许查询的数据范围")
    @TableField(exist = false)
    private List<String> permissions;

    @ApiModelProperty(value = "子母单")
    @TableField(exist = false)
    private String childParentCode;

    @ApiModelProperty(value = "子母单List")
    @TableField(exist = false)
    private List<String> childParentCodeList;

    public SysUser() {

    }

    public SysUser(Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public boolean isAdmin() {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId) {
        return userId != null && (1L == userId || 2L == userId);
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(Date loginDate) {
        this.loginDate = loginDate;
    }

    public String getServiceProduct() {
        return serviceProduct;
    }

    public void setServiceProduct(String serviceProduct) {
        this.serviceProduct = serviceProduct;
    }

    public String getSpearPassword() {
        return spearPassword;
    }

    public void setSpearPassword(String spearPassword) {
        this.spearPassword = spearPassword;
    }

    public SysSite getSite() {
        return site;
    }

    public void setSite(SysSite site) {
        this.site = site;
    }

    public List<SysRole> getRoles() {
        return roles;
    }

    public void setRoles(List<SysRole> roles) {
        this.roles = roles;
    }

    public Long[] getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(Long[] roleIds) {
        this.roleIds = roleIds;
    }

//    public Long[] getPostIds()
//    {
//        return postIds;
//    }
//
//    public void setPostIds(Long[] postIds)
//    {
//        this.postIds = postIds;
//    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("userId", getUserId())
                .append("siteCode", getSiteCode())
                .append("userName", getUserName())
                .append("nickName", getNickName())
                .append("email", getEmail())
                .append("phonenumber", getPhonenumber())
                .append("sex", getSex())
                .append("avatar", getAvatar())
                .append("password", getPassword())
                .append("spearPassword", getSpearPassword())
                .append("serviceProduct", getServiceProduct())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("loginIp", getLoginIp())
                .append("loginDate", getLoginDate())
                .append("createBy", getCreateBy())
                .append("createByName", getCreateByName())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateByName", getUpdateByName())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .append("site", getSite())
                .toString();
    }


}
