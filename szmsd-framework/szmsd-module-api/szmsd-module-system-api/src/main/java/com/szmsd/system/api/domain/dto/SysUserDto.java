package com.szmsd.system.api.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.annotation.Excel.ColumnType;
import com.szmsd.common.core.annotation.Excel.Type;
import com.szmsd.common.core.annotation.Excels;
import com.szmsd.common.core.web.domain.BaseEntity;
import com.szmsd.system.api.domain.SysRole;
import com.szmsd.system.api.domain.SysSite;
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


@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "用户对象Dto", description = "sys_user")
public class SysUserDto
{
    private static final long serialVersionUID = 1L;

    @TableId(value = "userId", type = IdType.AUTO)
    @ApiModelProperty(value = "用户id")
    @Excel(name = "用户序号", cellType = ColumnType.NUMERIC, prompt = "用户编号")
    @TableField(exist = false)
    private Long userId;

    @ApiModelProperty(value = "网点编号")
    @Excel(name = "网点编号", type = Type.IMPORT)
    private String siteCode;

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


    @ApiModelProperty(value = "巴枪使用密码")
    @Excel(name = "巴枪使用密码")
    private String spearPassword;



    @ApiModelProperty(value = "帐号状态（0正常 1停用）")
    @Excel(name = "帐号状态", readConverterExp = "0=正常,1=停用")
    private String status;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "创建者ID")
    private String createBy;

    @ApiModelProperty(value = "创建者")
    private String createByName;

    @ApiModelProperty(value = "更新者ID")
    private String updateBy;

    @ApiModelProperty(value = "更新者")
    private String updateByName;

    @ApiModelProperty(value = "客户代码")
    private String sellerCode;

    @ApiModelProperty(value = "角色组")
    @TableField(exist = false)
    private Long[] roleIds;

//    @ApiModelProperty(value = "岗位组")
//    @TableField(exist = false)
//    private Long[] postIds;

    public SysUserDto()
    {

    }

    public SysUserDto(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public boolean isAdmin()
    {
        return isAdmin(this.userId);
    }

    public static boolean isAdmin(Long userId)
    {
        return userId != null && 1L == userId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    @Size(min = 0, max = 30, message = "用户昵称长度不能超过30个字符")
    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 0, max = 30, message = "用户账号长度不能超过30个字符")
    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    @Size(min = 0, max = 11, message = "手机号码长度不能超过11个字符")
    public String getPhonenumber()
    {
        return phonenumber;
    }






    @JsonProperty
    public String getPassword()
    {
        return password;
    }




    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
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
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .toString();
    }


}
