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

import java.util.Date;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-07-07
 */
@TableName("bas_destination")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasDestination对象", description = "")
public class BasDestination  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "当前id")
    @Excel(name = "当前id")
    private String currentId;

    @ApiModelProperty(value = "父级id")
    @Excel(name = "父级id")
    private String fatherId;

    @ApiModelProperty(value = "地区编号")
    @Excel(name = "地区编号")
    private String regionCode;

    @ApiModelProperty(value = "地区名称")
    @Excel(name = "地区名称")
    private String regionName;

    @ApiModelProperty(value = "快件类型")
    @Excel(name = "快件类型")
    private String expressType;

    @ApiModelProperty(value = "所属大区")
    @Excel(name = "所属大区")
    private String blongRegion;

    @ApiModelProperty(value = "营业网点")
    @Excel(name = "营业网点")
    private String businesSite;

    @ApiModelProperty(value = "城市名称")
    @Excel(name = "城市名称")
    private String cityName;

    @ApiModelProperty(value = "省名称")
    @Excel(name = "省名称")
    private String provinceName;

    @ApiModelProperty(value = "区/县名称")
    @Excel(name = "区/县名称")
    private String areaName;

    @ApiModelProperty(value = "派件网点")
    @Excel(name = "派件网点")
    private String disSite;

    @ApiModelProperty(value = "派送范围")
    @Excel(name = "派送范围")
    private String disRange;

    @ApiModelProperty(value = "到付折扣")
    @Excel(name = "到付折扣")
    private String ponDiscount;

    @ApiModelProperty(value = "代收折扣")
    @Excel(name = "代收折扣")
    private String cotDiscount;

    @ApiModelProperty(value = "分拨标识")
    @Excel(name = "分拨标识")
    private String allIdentity;

    @ApiModelProperty(value = "允许到付标识（1=允许，2=不允许）")
    @Excel(name = "允许到付标识（1=允许，2=不允许）")
    private String allowPon;

    @ApiModelProperty(value = "分拨费收取标识 (1=收，2=不收)")
    @Excel(name = "分拨费收取标识 (1=收，2=不收)")
    private String remFee;

    @ApiModelProperty(value = "收取货款手续费标识(1=收，2=不收)")
    @Excel(name = "收取货款手续费标识(1=收，2=不收)")
    private String seIdc;

    @ApiModelProperty(value = "代收货款限额 ")
    @Excel(name = "代收货款限额 ")
    private String allQuota;

    @ApiModelProperty(value = "预留字段")
    @Excel(name = "预留字段")
    private String parm;

    @ApiModelProperty(value = "代收货款标识(1=收，2=不收))")
    @Excel(name = "代收货款标识(1=收，2=不收)")
    private String saIdc;

    @ApiModelProperty(value = "输入框")
    @Excel(name = "输入框")
    private String asFee;

    @ApiModelProperty(value = "所属区域")
    @Excel(name = "所属区域")
    private String blonga;

    @ApiModelProperty(value = "备注")
    @Excel(name = "备注")
    private String remark;

    @ApiModelProperty(value = "区")
    @Excel(name = "区")
    private String area;

    @ApiModelProperty(value = "市")
    @Excel(name = "市")
    private String city;

    @ApiModelProperty(value = "省")
    @Excel(name = "省")
    private String province;

    @ApiModelProperty(value = "派件网点code")
    @Excel(name = "派件网点code")
    private String disSiteCode;

    @ApiModelProperty(value = "营业件网点code")
    @Excel(name = "营业网点code")
    private String businesSiteCode;

    @ApiModelProperty(value = "目的地类型code")
    @Excel(name = "目的地类型code")
    private String destinationTypeCode;

    @ApiModelProperty(value = "目的地类型")
    @Excel(name = "目的地类型")
    private String destinationTypeName;

    @ApiModelProperty(value = "创建人")
    @Excel(name = "创建人")
    private String createByName;

    @ApiModelProperty(value = "修改人name")
    @Excel(name = "修改人name")
    private String updateByName;

    @ApiModelProperty(value = "创建人id")
    @Excel(name = "创建人id")
    private String createBy;

    @ApiModelProperty(value = "修改人id")
    @Excel(name = "修改人id")
    private String updateBy;

    @ApiModelProperty(value = "修改时间")
    @Excel(name = "修改时间")
    private Date updateTime;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "分拣码")
    @Excel(name = "分拣码")
    private String sortingCode;
}
