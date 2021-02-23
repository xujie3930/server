package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-08-11
 */
@TableName("bas_user")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasUser对象", description = "客户员工表")
public class BasUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(type=IdType.AUTO)
    private int id;

    @ApiModelProperty(value = "用户编号")
    @Excel(name = "用户编号")
    private String userCode;

    @ApiModelProperty(value = "登录密码")
    @Excel(name = "登录密码")
    private String password;

    @ApiModelProperty(value = "姓名")
    @Excel(name = "姓名")
    private String name;

    @ApiModelProperty(value = "性别 0=女 1男")
    @Excel(name = "性别 0=女 1男")
    private String sex;

    @ApiModelProperty(value = "电话")
    @Excel(name = "电话")
    private String phone;

    @ApiModelProperty(value = "地址")
    @Excel(name = "地址")
    private String address;

    @ApiModelProperty(value = "客户类型")
    @Excel(name = "客户类型")
    private String cusType;

    @ApiModelProperty(value = "客户编号")
    @Excel(name = "客户编号")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "微信号")
    @Excel(name = "微信号")
    private String weChat;

    @ApiModelProperty(value = "证件类型")
    @Excel(name = "证件类型")
    private String idType;

    @ApiModelProperty(value = "证件号")
    @Excel(name = "证件号")
    private String idNumber;

    @ApiModelProperty(value = "证件人名称")
    @Excel(name = "证件人名称")
    private String idName;

    @ApiModelProperty(value = "证件国家")
    @Excel(name = "证件国家")
    private String idCountry;

    @ApiModelProperty(value = "证件省")
    @Excel(name = "证件省")
    private String idProvince;

    @ApiModelProperty(value = "证件市")
    @Excel(name = "证件市")
    private String idCity;

    @ApiModelProperty(value = "证件区")
    @Excel(name = "证件区")
    private String idArea;

    @ApiModelProperty(value = "证件详细地址")
    @Excel(name = "证件详细地址")
    private String idAddress;

    @ApiModelProperty(value = "用户类型1=个人用户，2=内部用户")
    @Excel(name = "用户类型1=个人用户，2=内部用户")
    private String userType;

    @ApiModelProperty(value = "收集方式")
    @Excel(name = "收集方式")
    private String collectionType;

    @ApiModelProperty(value = "图片地址")
    @Excel(name = "图片地址")
    private String imgUrl;

    @ApiModelProperty(value = "业务员code")
    @Excel(name = "业务员code")
    private String empCode;

    @ApiModelProperty(value = "业务员name")
    @Excel(name = "业务员name")
    private String empName;

    @ApiModelProperty(value = "所属网点code")
    @Excel(name = "所属网点code")
    private String siteCode;

    @ApiModelProperty(value = "所属网点名称")
    @Excel(name = "所属网点名称")
    private String siteName;

    @ApiModelProperty(value = "创建人名称")
    @Excel(name = "创建人名称")
    private String createName;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateName;

    @ApiModelProperty(value = "指定业务员编号")
    @Excel(name = "指定业务员编号")
    private String empsCode;

    @ApiModelProperty(value = "指定业务员名称")
    @Excel(name = "指定业务员名称")
    private String empsName;

    @ApiModelProperty(value = "用户名")
    @Excel(name = "用户名")
    private String nickName;

    @ApiModelProperty(value = "管理标识 0=非管理  1=管理")
    @Excel(name = "管理标识 0=非管理  1=管理")
    private String admIden;

    @ApiModelProperty(value = "启用标识 0=未启用，1启用")
    @Excel(name = "启用标识 0=未启用，1启用")
    private String enableIden;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "关联id")
    @Excel(name = "关联id")
    private long userId;



}
