package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("bas_employees")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasEmployees对象", description = "")
public class BasEmployees {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    private String id;

    @ApiModelProperty(value = "员工编号")
    @Excel(name = "员工编号")
    private String empCode;

    @ApiModelProperty(value = "员工名称（中）")
    @Excel(name = "员工名称（中）")
    private String empName;

    @ApiModelProperty(value = "员工名称（英）")
    @Excel(name = "员工名称（英）")
    private String empNameEn;

    @ApiModelProperty(value = "员工名称（阿拉伯）")
    @Excel(name = "员工名称（阿拉伯）")
    private String empNameAr;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String empPhone;

    @ApiModelProperty(value = "联系人手机")
    @Excel(name = "联系人手机")
    private String empTel;

    @ApiModelProperty(value = "网点编号")
    @Excel(name = "网点编号")
    private String siteCode;

    @ApiModelProperty(value = "网点名称")
    @Excel(name = "网点名称")
    private String siteName;

    @ApiModelProperty(value = "所属岗位")
    @Excel(name = "所属岗位")
    private String blongJobs;

    @ApiModelProperty(value = "服务产品")
    @Excel(name = "服务产品")
    private String serviceProduct;

    @ApiModelProperty(value = "0表示离职 1表示在职 2表示请假")
    @Excel(name = "0表示离职 1表示在职 2表示请假")
    private String empState;

    @ApiModelProperty(value = "巴枪使用密码")
    @Excel(name = "巴枪使用密码")
    private String spearPassword;

    @ApiModelProperty(value = "录入人名称")
    @Excel(name = "录入人名称")
    private String createByName;

    @ApiModelProperty(value = "录入网点")
    @Excel(name = "录入网点")
    private String createSite;

    @ApiModelProperty(value = "修改人名")
    @Excel(name = "修改人名")
    private String updateByName;

    @ApiModelProperty(value = "预留1")
    @Excel(name = "预留1")
    private String pram1;

    @ApiModelProperty(value = "预留2")
    @Excel(name = "预留2")
    private String pram2;

    @ApiModelProperty(value = "预留3")
    @Excel(name = "预留3")
    private String pram3;

    @ApiModelProperty(value = "预留4")
    @Excel(name = "预留4")
    private String pram4;

    @ApiModelProperty(value = "预留5")
    @Excel(name = "预留5")
    private String pram5;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private String version;

    @TableField(exist = false)
    @ApiModelProperty(value = "开始时间")
    @Excel(name = "开始时间")
    private String startTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "结束时间")
    @Excel(name = "结束时间")
    private String endTime;

    @ApiModelProperty(value = "录入时间")
    @Excel(name = "录入时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "密码")
    @Excel(name = "密码")
    private String password;

    @ApiModelProperty(value = "用户id")
    @Excel(name = "用户id")
    private long userId;

    @TableField(exist = false)
    @ApiModelProperty(value = "网点List")
    @Excel(name = "网点List")
    private String siteCodeList;

    @ApiModelProperty(value = "员工名称集合",hidden = true)
    @TableField(exist = false)
    private List<String> empNameList;

    @ApiModelProperty(value = "员工编码集合",hidden = true)
    @TableField(exist = false)
    private List<String> empCodeList;
}
