package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
@Data
@Accessors(chain = true)
@ApiModel(value = "", description = "DelTimeliness对象")
public class DelTimeliness extends QueryDto {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "发货服务")
    @Excel(name = "发货服务")
    private String shipmentRule;

    @ApiModelProperty(value = "国家code")
    @Excel(name = "国家code")
    private String countryCode;

    @ApiModelProperty(value = "国家名字")
    @Excel(name = "国家名字")
    private String country;

    @ApiModelProperty(value = "时区一")
    @Excel(name = "时区一")
    private String scopeOne;

    @ApiModelProperty(value = "时区二")
    @Excel(name = "时区二")
    private String scopeTwo;

    @ApiModelProperty(value = "时区三")
    @Excel(name = "时区三")
    private String scopeThree;

    @ApiModelProperty(value = "时区四")
    @Excel(name = "时区四")
    private String scopeFour;

    @ApiModelProperty(value = "时区五")
    @Excel(name = "时区五")
    private String scopeFive;

    @ApiModelProperty(value = "创建时间")
    @Excel(name = "创建时间")
    private Date createTime;
}