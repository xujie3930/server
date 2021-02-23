package com.szmsd.system.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.annotation.Excel.ColumnType;
import com.szmsd.common.core.web.domain.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 系统访问记录表 sys_logininfor
 * 
 * @author lzw
 */
@TableName("sys_logininfor")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "系统访问记录表", description = "sys_logininfor")
public class SysLogininfor extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(value = "infoId", type = IdType.AUTO)
    @ApiModelProperty(value = "主键id")
    @Excel(name = "序号", cellType = ColumnType.NUMERIC)
    private Long infoId;

    @Excel(name = "用户账号")
    @ApiModelProperty(value = "用户账号")
    private String userName;

    @Excel(name = "登录状态", readConverterExp = "0=成功,1=失败")
    @ApiModelProperty(value = "登录状态 0成功 1失败")
    private String status;

    @Excel(name = "登录地址")
    @ApiModelProperty(value = "登录IP地址")
    private String ipaddr;

    @Excel(name = "登录地点")
    @ApiModelProperty(value = "登录地点")
    private String loginLocation;

    @Excel(name = "浏览器")
    @ApiModelProperty(value = "浏览器类型")
    private String browser;

    @Excel(name = "操作系统")
    @ApiModelProperty(value = "操作系统")
    private String os;

    @Excel(name = "提示消息")
    @ApiModelProperty(value = "提示消息")
    private String msg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "访问时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "访问时间")
    private Date loginTime;

    public Long getInfoId()
    {
        return infoId;
    }

    public void setInfoId(Long infoId)
    {
        this.infoId = infoId;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getIpaddr()
    {
        return ipaddr;
    }

    public void setIpaddr(String ipaddr)
    {
        this.ipaddr = ipaddr;
    }

    public String getLoginLocation()
    {
        return loginLocation;
    }

    public void setLoginLocation(String loginLocation)
    {
        this.loginLocation = loginLocation;
    }

    public String getBrowser()
    {
        return browser;
    }

    public void setBrowser(String browser)
    {
        this.browser = browser;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public String getMsg()
    {
        return msg;
    }

    public void setMsg(String msg)
    {
        this.msg = msg;
    }

    public Date getLoginTime()
    {
        return loginTime;
    }

    public void setLoginTime(Date loginTime)
    {
        this.loginTime = loginTime;
    }
}