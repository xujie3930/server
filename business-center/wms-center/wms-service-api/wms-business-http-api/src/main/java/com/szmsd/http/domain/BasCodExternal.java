package com.szmsd.http.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@ApiModel(value = "Cod表", description = "Cod表")
public class BasCodExternal {


    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称(英文)")
    private String countryEnName;

    @ApiModelProperty(value = "国家名称(中文)")
    private String countryCnName;

    @ApiModelProperty(value = "源币种代码")
    private String sourceCurrencyCode;

    @ApiModelProperty(value = "源币种名称")
    private String SourceCurrencyName;

    @ApiModelProperty(value = "目标币种代码")
    private String sargetCurrencyCode;

    @ApiModelProperty(value = "目标币种代码")
    private String targetCurrencyCode;


    @ApiModelProperty(value = "目标币种名称")
    private String targetCurrencyName;

    @ApiModelProperty(value = "汇率")
    private Double rate;

    @ApiModelProperty(value = "更新时间")
    private String updatedTime;

    @ApiModelProperty(value = "更新时间")
    private Date updatedTimes;
}