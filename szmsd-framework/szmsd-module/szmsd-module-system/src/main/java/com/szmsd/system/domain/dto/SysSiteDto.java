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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * 网点管理
 * </p>
 *
 * @author lzw
 * @since 2020-06-19
 */
@Data
@ApiModel(value = "网点DTO对象", description = "SysSiteDto")
public class SysSiteDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "网点id")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "网点id")
    private Long id;

    @ApiModelProperty(value = "父网点id")
    @Excel(name = "父网点id")
    private Long parentId;

    @ApiModelProperty(value = "父网点名称")
    private String parentName;

    @ApiModelProperty(value = "祖级列表")
    @Excel(name = "祖级列表")
    private String ancestors;

    @ApiModelProperty(value = "网点编号")
    @Excel(name = "网点编号")
    private String siteCode;

    @ApiModelProperty(value = "显示顺序")
    @Excel(name = "显示顺序")
    private Integer orderNum;

    @ApiModelProperty(value = "网点名称-中文")
    @Excel(name = "网点名称-中文")
    private String siteNameChinese;

    @ApiModelProperty(value = "关键字编号")
    @Excel(name = "关键字编号")
    private String keywordCode;

    @ApiModelProperty(value = "关键字名称")
    @Excel(name = "关键字名称")
    private String keywordName;

//    @ApiModelProperty(value = "网点名称-阿拉伯")
//    @Excel(name = "网点名称-阿拉伯")
//    private String siteNameArabic;
//
//    @ApiModelProperty(value = "网点名称-英语")
//    @Excel(name = "网点名称-英语")
//    private String siteNameEnglish;

    @ApiModelProperty(value = "机构网点级别")
    @Excel(name = "机构网点级别")
    private String siteRankCode;

    @ApiModelProperty(value = "机构网点级别名称")
    @Excel(name = "机构网点级别")
    private String siteRankName;

    @ApiModelProperty(value = "代收货款币种")
    @Excel(name = "代收货款币种")
    private String codCurrency;

    @ApiModelProperty(value = "负责人")
    @Excel(name = "负责人")
    private String leader;

    @ApiModelProperty(value = "手机")
    @Excel(name = "手机")
    private String mobile;


    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String phone;

    @ApiModelProperty(value = "类型code")
    @Excel(name = "类型code")
    private String typeCode;

    @ApiModelProperty(value = "类型")
    @Excel(name = "类型")
    private String typeName;

    @ApiModelProperty(value = "邮箱")
    @Excel(name = "邮箱")
    private String email;

    @ApiModelProperty(value = "默认发件地")
    @Excel(name = "默认发件地")
    private String sendAddr;

    @ApiModelProperty(value = "国家")
    @Excel(name = "国家")
    private String country;

    @ApiModelProperty(value = "省份")
    @Excel(name = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    @Excel(name = "城市")
    private String city;

    @ApiModelProperty(value = "区域")
    @Excel(name = "区域")
    private String area;
    @ApiModelProperty(value = "所属地区")
    @Excel(name = "所属地区")
    private String region;

    @ApiModelProperty(value = "街道")
    @Excel(name = "街道")
    private String street;

    @ApiModelProperty(value = "详细地址")
    @Excel(name = "详细地址")
    private String address;

    @ApiModelProperty(value = "所属财务中心Code")
    @Excel(name = "所属财务中心code")
    private String finCenterSiteCode;

    @ApiModelProperty(value = "所属财务中心")
    @Excel(name = "所属财务中心")
    private String finCenterSiteName;

    @ApiModelProperty(value = "财务中心标识：0-没有，1-有")
    @Excel(name = "财务中心标识：0-没有，1-有")
    private String finCenterFlag;

    @ApiModelProperty(value = "分拨重虚拟标识：0-无，1-有")
    @Excel(name = "分拨重虚拟标识：0-无，1-有")
    private String allocateCenterFlag;

    @ApiModelProperty(value = "是否到付：0-否，1-是")
    @Excel(name = "是否到付：0-否，1-是")
    private String reachPayFlag;

    @ApiModelProperty(value = "允许代缴：0-不允许，1-允许")
    @Excel(name = "允许代缴：0-不允许，1-允许")
    private String loanFlag;

    @ApiModelProperty(value = "代收贷款限额")
    @Excel(name = "代收贷款限额")
    private BigDecimal loanPaymentQuota;

    @ApiModelProperty(value = "管理方式Code")
    @Excel(name = "管理方式Code")
    private String supervisorModeCode;

    @ApiModelProperty(value = "管理方式")
    @Excel(name = "管理方式")
    private String supervisorModeName;

    @ApiModelProperty(value = "派送范围")
    @Excel(name = "派送范围")
    private String deliveryScope;

    @ApiModelProperty(value = "分拣码")
    @Excel(name = "分拣码")
    private String sortingCode;

    @ApiModelProperty(value = "网点状态（0正常 1停用）")
    @Excel(name = "网点状态（0正常 1停用）")
    private String status;




    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;


    

}
