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


/**
 * <p>
 * 国家表
 * </p>
 *
 * @author ziling
 * @since 2020-08-10
 */
@TableName("bas_country")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@SuppressWarnings("serial")
@Data
@ApiModel(value = "BasCountry对象", description = "国家表")
public class BasCountry {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @Excel(name = "主键id")
    @TableId(value = "id", type = IdType.UUID)
    private String id;

    @ApiModelProperty(value = "国家id")
    @Excel(name = "国家id")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    @Excel(name = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "国家名称（英文）")
    @Excel(name = "国家名称（英文）")
    private String countryNameEn;

    @ApiModelProperty(value = "国家（英文）简称")
    @Excel(name = "国家（英文）简称")
    private String countryAdd;

    @ApiModelProperty(value = "国家名称（阿拉伯）")
    @Excel(name = "国家名称（阿拉伯）")
    private String countryNameAr;
}
