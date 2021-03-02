package com.szmsd.bas.api.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-06-29
 */
@TableName("bas_cusprice")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasCusprice对象", description = "")
public class BasCusprice {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    private String id;

    @ApiModelProperty(value = "客户id")
    @Excel(name = "客户id")
    private String cusCode;

    @ApiModelProperty(value = "客户名称")
    @Excel(name = "客户名称")
    private String cusName;

    @ApiModelProperty(value = "产品类型id")
    @Excel(name = "产品类型id")
    private String productTypeCode;

    @ApiModelProperty(value = "产品类型名称")
    @Excel(name = "产品类型名称")
    private String productTypeName;

    @ApiModelProperty(value = "主类别id")
    @Excel(name = "主类别id")
    private String mainCode;

    @ApiModelProperty(value = "费用类别")
    @Excel(name = "费用类别")
    private String costCategory;

    @ApiModelProperty(value = "子类别id")
    @Excel(name = "子类别id")
    private String subCode;

    @ApiModelProperty(value = "录入网点")
    @Excel(name = "录入网点")
    private String createSite;

    @ApiModelProperty(value = "计费模式（0=实际重量，1=进位舍一，2=进位舍二）")
    @Excel(name = "计费模式（0=实际重量，1=进位舍一，2=进位舍二）")
    private String chargingPattern;

    @ApiModelProperty(value = "有效起始时间")
    @Excel(name = "有效起始时间")
    private String startTime;

    @ApiModelProperty(value = "有效终止时间")
    @Excel(name = "有效终止时间")
    private String endTime;

    @ApiModelProperty(value = "网点编号")
    @Excel(name = "网点编号")
    private String siteCode;

    @ApiModelProperty(value = "网点名称")
    @Excel(name = "网点名称")
    private String siteName;

    @ApiModelProperty(value = "重量段")
    @Excel(name = "重量段")
    private String weight;

    @ApiModelProperty(value = "计算公式")
    @Excel(name = "计算公式")
    private String calculateFormula;

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

    @ApiModelProperty(value = "创建者id")
    @Excel(name = "创建者id")
    private String createId;

    @ApiModelProperty(value = "修改者id")
    @Excel(name = "修改者id")
    private String updateId;

    @ApiModelProperty(value = "删除标志（0代表存在 2代表删除）")
    @Excel(name = "删除标志（0代表存在 2代表删除）")
    private String delFlag;

    @ApiModelProperty(value = "状态（0正常 1停用）")
    @Excel(name = "状态（0正常 1停用）")
    private String status;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private String version;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private Date remark;

    @TableField(exist = false)
    @ApiModelProperty(value = "当前时间")
    @Excel(name = "当前时间")
    private String time;

    @TableField(exist = false)
    @ApiModelProperty(value = "重量")
    @Excel(name = "重量")
    private String heft;
}
