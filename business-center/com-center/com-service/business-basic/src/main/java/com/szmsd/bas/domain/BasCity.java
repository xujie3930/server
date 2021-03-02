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
 * 城市表
 * </p>
 *
 * @author ziling
 * @since 2020-08-03
 */
@TableName("bas_city")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasCity对象", description = "城市表")
public class BasCity  {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "城市id")
    @Excel(name = "城市id")
    private String cityCode;

    @ApiModelProperty(value = "城市名")
    @Excel(name = "城市名")
    private String cityName;

    @ApiModelProperty(value = "城市名英")
    @Excel(name = "城市名英")
    private String cityNameEn;

    @ApiModelProperty(value = "城市名阿拉伯")
    @Excel(name = "城市名阿拉伯")
    private String cityNameAr;

    @ApiModelProperty(value = "邮编")
    @Excel(name = "邮编")
    private String postcode;

    @ApiModelProperty(value = "省份id")
    @Excel(name = "省份id")
    private String provinceCode;

    @ApiModelProperty(value = "省份名称")
    @Excel(name = "省份名称")
    private String provinceName;

    @ApiModelProperty(value = "国家id")
    @Excel(name = "国家id")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "国家名称英")
    @Excel(name = "国家名称英")
    private String countryNameEn;

    @ApiModelProperty(value = "国家名称阿拉伯")
    @Excel(name = "国家名称阿拉伯")
    private String countryNameAr;
}
