package com.szmsd.bas.domain.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author chenanze
 * @date 2020-11-21
 * @description
 */
@Data
public class basSubDto {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "子类id")
    @Excel(name = "子类id")
    private String subCode;

    @ApiModelProperty(value = "子类名称")
    @Excel(name = "子类名称")
    private String subName;

    @ApiModelProperty(value = "子类名称（英）")
    @Excel(name = "子类名称（英）")
    private String subNameEn;

    @ApiModelProperty(value = "子类名称（阿拉伯）")
    @Excel(name = "子类名称（阿拉伯）")
    private String subNameAr;

    @ApiModelProperty(value = "主类id")
    @Excel(name = "主类id")
    private String mainCode;

    @ApiModelProperty(value = "主类名称")
    @Excel(name = "主类名称")
    private String mainName;

    @ApiModelProperty(value = "字典排序")
    @Excel(name = "字典排序")
    private int sort;

    @ApiModelProperty(value = "子类别值")
    @Excel(name = "子类别值")
    private String subValue;

}
