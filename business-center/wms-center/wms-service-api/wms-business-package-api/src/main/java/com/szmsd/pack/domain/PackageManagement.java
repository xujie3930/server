package com.szmsd.pack.domain;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.szmsd.common.core.web.domain.BaseEntity;

import java.util.Date;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.pack.config.BOConvert;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.szmsd.common.core.annotation.Excel;


/**
 * <p>
 * package - 交货管理 - 地址信息表
 * </p>
 *
 * @author 11
 * @since 2021-04-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value = "package - 交货管理 - 地址信息表", description = "PackageManagement对象")
public class PackageManagement extends BaseEntity implements BOConvert {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    @Excel(name = "修改人")
    private String updateBy;

    @ApiModelProperty(value = "版本号")
    @Excel(name = "版本号")
    private BigDecimal version;

    @ApiModelProperty(value = "逻辑删除标识；2-已删除，0-未删除")
    @Excel(name = "逻辑删除标识；2-已删除，0-未删除")
    private Integer delFlag;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "用户code")
    @Excel(name = "用户code")
    private String sellerCode;

    @ApiModelProperty(value = "联系人姓名")
    @Excel(name = "联系人姓名")
    private String linkUserName;

    @ApiModelProperty(value = "联系电话")
    @Excel(name = "联系电话")
    private String linkPhone;

    @ApiModelProperty(value = "国家 - 名称")
    @Excel(name = "国家 - 名称")
    private String countryNameZh;

    @ApiModelProperty(value = "国家 - 简码")
    @Excel(name = "国家 - 简码")
    private String countryCode;

    @ApiModelProperty(value = "国家 - 英文名")
    @Excel(name = "国家 - 英文名")
    private String countryNameEn;

    @ApiModelProperty(value = "省 - 名称")
    @Excel(name = "省 - 名称")
    private String provinceNameZh;

    @ApiModelProperty(value = "省 - 简码")
    @Excel(name = "省 - 简码")
    private String provinceCode;

    @ApiModelProperty(value = "省 - 英文名")
    @Excel(name = "省 - 英文名")
    private String provinceNameEn;

    @ApiModelProperty(value = "市 - 名称")
    @Excel(name = "市 - 名称")
    private String cityNameZh;

    @ApiModelProperty(value = "市 - 简码")
    @Excel(name = "市 - 简码")
    private String cityCode;

    @ApiModelProperty(value = "市 - 英文名")
    @Excel(name = "市 - 英文名")
    private String cityNameEn;

    @ApiModelProperty(value = "区 - 名称")
    @Excel(name = "区 - 名称")
    private String districtNameZh;

    @ApiModelProperty(value = "区 - 简码")
    @Excel(name = "区 - 简码")
    private String districtCode;

    @ApiModelProperty(value = "区 - 英文名")
    @Excel(name = "区 - 英文名")
    private String districtNameEn;

    @ApiModelProperty(value = "具体地址 - 中文名")
    @Excel(name = "具体地址 - 中文名")
    private String addressZh;

    @ApiModelProperty(value = "实际揽收地址")
    @Excel(name = "实际揽收地址")
    private String deliveryAddress;

    @ApiModelProperty(value = "具体地址 - 英文名")
    @Excel(name = "具体地址 - 英文名")
    private String addressEn;

    @ApiModelProperty(value = "提交时间")
    @Excel(name = "提交时间")
    private Date submitTime;

    @ApiModelProperty(value = "是否导出【 0：未导出，1：已导出】")
    @Excel(name = "是否导出【 0：未导出，1：已导出】")
    private Integer exportType;

    @ApiModelProperty(value = "导出时间")
    @Excel(name = "导出时间")
    private Date exportTime;

    @ApiModelProperty(value = "实际揽件时间")
    @Excel(name = "实际揽件时间")
    private Date deliveryTime;

    @ApiModelProperty(value = "期望收货日期")
    @Excel(name = "期望收货日期")
    private Date expectedDeliveryTime;

    @ApiModelProperty(value = "实际揽收数量")
    @Excel(name = "实际揽收数量")
    private Integer receivePackageNum;

    @ApiModelProperty(value = "司机姓名")
    @Excel(name = "司机姓名")
    private String driverName;

    @ApiModelProperty(value = "司机号码")
    @Excel(name = "司机号码")
    private String driverPhone;

    @ApiModelProperty(value = "揽件数量")
    @Excel(name = "揽件数量")
    private Integer packageNum;

    @ApiModelProperty(value = "货物类型【 0：入库，1：转运】")
    @Excel(name = "货物类型【 0：入库，1：转运】")
    private Integer operationType;

}
