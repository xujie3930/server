package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-06-19
 */
@TableName("bas_customer")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasCustomer对象", description = "")
public class BasCustomer {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    private String id;

    @ApiModelProperty(value = "客户编号")
    @Excel(name = "Customer number")
    private String cusCode;

    @ApiModelProperty(value = "客户简称")
    @Excel(name = "Customer abbreviation")
    private String cusAbbverviation;

    @ApiModelProperty(value = "客户名称（中）")
    @Excel(name = "客户名称（中）")
    private String cusName;

    @ApiModelProperty(value = "客户名称（英）")
    @Excel(name = "客户名称（英）")
    private String cusNameEn;

    @ApiModelProperty(value = "客户名称（阿拉伯）")
    @Excel(name = "客户名称（阿拉伯）")
    private String cusNameAr;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String cusPhone;

    @ApiModelProperty(value = "手机号码")
    @Excel(name = "Customer tell")
    private String cusTle;

    @ApiModelProperty(value = "网点编号")
    @Excel(name = "Site code")
    private String siteCode;

    @ApiModelProperty(value = "网点名称")
    @Excel(name = "Site name")
    private String siteName;

    @ApiModelProperty(value = "所属员工编号")
    @Excel(name = "所属员工编号")
    private String employeeCode;

    @ApiModelProperty(value = "所属员工名称")
    @Excel(name = "所属员工名称")
    private String employeeName;

    @ApiModelProperty(value = "所属地址")
    @Excel(name = "所属地址")
    private String addressName;

    @ApiModelProperty(value = "付款方式")
    @Excel(name = "付款方式")
    private String termsOfPayment;

    @ApiModelProperty(value = "付款方式code")
    @Excel(name = "付款方式code")
    private String termsOfPaymentCode;

    @ApiModelProperty(value = "vip平台登录密码")
    @Excel(name = "vip平台登录密码")
    private String vipPassword;

    @ApiModelProperty(value = "录入人")
    @Excel(name = "录入人")
    private String createByName;

    @ApiModelProperty(value = "录入网点")
    @Excel(name = "录入网点")
    private String createSite;

    @ApiModelProperty(value = "修改者ID")
    @Excel(name = "修改者ID")
    private String updateBy;

    @ApiModelProperty(value = "创建ID")
    @Excel(name = "创建ID")
    private String createId;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @Excel(name = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @Excel(name = "状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private String version;

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

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    @Excel(name = "开始时间")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    @Excel(name = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateByName;

    @ApiModelProperty(value = "保价标识")
    @Excel(name = "保价标识")
    private String premiumIden;

    @ApiModelProperty(value = "保价限额")
    @Excel(name = "保价限额")
    private BigDecimal premiumQuota;

    @ApiModelProperty(value = "保价费率")
    @Excel(name = "保价费率")
    private BigDecimal premiumRate;

    @ApiModelProperty(value = "代收货款标识")
    @Excel(name = "代收货款标识")
    private String feeIden;

    @ApiModelProperty(value = "代收货款限额")
    @Excel(name = "代收货款限额")
    private String feeQuota;

    @ApiModelProperty(value = "代收货款费率")
    @Excel(name = "代收货款费率")
    private BigDecimal feeRate;

    @TableField(exist = false)
    @ApiModelProperty(value = "图片")
    @Excel(name = "图片")
    private List picture;

    @ApiModelProperty(value = "图片1")
    @Excel(name = "图片1")
    private String picture1;

    @ApiModelProperty(value = "图片2")
    @Excel(name = "图片2")
    private String picture2;

    @ApiModelProperty(value = "图片3")
    @Excel(name = "图片3")
    private String picture3;

    @ApiModelProperty(value = "图片4")
    @Excel(name = "图片4")
    private String picture4;

    @ApiModelProperty(value = "图片5")
    @Excel(name = "图片5")
    private String picture5;

    @TableField(exist = false)
    @ApiModelProperty(value = "错误消息")
    @Excel(name = "错误消息")
    private String message;

    @ApiModelProperty(value = "客户编号集")
    @TableField(exist = false)
    private List<String> cusCodeList;
}
