package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 * 省份表
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */
@TableName("bas_province")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasProvince对象", description = "省份表")
public class BasProvince  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "省份code")
    @Excel(name = "省份code")
    private String provinceCode;

    @ApiModelProperty(value = "省份名称")
    @Excel(name = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "省份名称英")
    @Excel(name = "省份名称英")
    private String provinceNameEn;

    @ApiModelProperty(value = "省份名称阿拉伯")
    @Excel(name = "省份名称阿拉伯")
    private String provinceNameAr;

    @ApiModelProperty(value = "省份简称")
    @Excel(name = "省份简称")
    private String provinceAbb;

    @ApiModelProperty(value = "国家id")
    @Excel(name = "国家id")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "国家名称（英文）")
    @Excel(name = "国家名称（英文）")
    private String countryNameEn;

    @ApiModelProperty(value = "国家名称（阿拉伯）")
    @Excel(name = "国家名称（阿拉伯）")
    private String countryNameAr;

}
