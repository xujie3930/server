package com.szmsd.bas.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * <p>
 *
 * </p>
 *
 * @author ziling
 * @since 2020-07-04
 */
@TableName("bas_area")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasArea对象", description = "")
public class BasArea {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    private String id;

    @ApiModelProperty(value = "区/县id")
    @Excel(name = "区/县id")
    private String areaCode;

    @ApiModelProperty(value = "区/县名称")
    @Excel(name = "区/县名称")
    private String areaName;

    @ApiModelProperty(value = "城市id")
    @Excel(name = "城市id")
    private String cityCode;

    @ApiModelProperty(value = "城市名称")
    @Excel(name = "城市名称")
    private String cityName;

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


}
