package com.szmsd.bas.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BaseProductMeasureDto {
    @ApiModelProperty(value = "产品编码")
    @Excel(name = "产品编码")
    @TableField("`code`")
    private String code;

    @ApiModelProperty(value = "初始重量g")
    @Excel(name = "初始重量g")
    private Double initWeight;

    @ApiModelProperty(value = "初始长 cm")
    @Excel(name = "初始长 cm")
    private Double initLength;

    @ApiModelProperty(value = "初始宽 cm")
    @Excel(name = "初始宽 cm")
    private Double initWidth;

    @ApiModelProperty(value = "初始高 cm")
    @Excel(name = "初始高 cm")
    private Double initHeight;

    @ApiModelProperty(value = "初始体积 cm3")
    @Excel(name = "初始体积 cm3")
    private BigDecimal initVolume;

    @ApiModelProperty(value = "仓库测量重量g")
    @Excel(name = "仓库测量重量g")
    private Double weight;

    @ApiModelProperty(value = "仓库测量长 cm")
    @Excel(name = "仓库测量长 cm")
    private Double length;

    @ApiModelProperty(value = "仓库测量宽 cm")
    @Excel(name = "仓库测量宽 cm")
    private Double width;

    @ApiModelProperty(value = "仓库测量高 cm")
    @Excel(name = "仓库测量高 cm")
    private Double height;

    @ApiModelProperty(value = "仓库测量体积 cm3")
    @Excel(name = "仓库测量体积 cm3")
    private BigDecimal Volume;
}
