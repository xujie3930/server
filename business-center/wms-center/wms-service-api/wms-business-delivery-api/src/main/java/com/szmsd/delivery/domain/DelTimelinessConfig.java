package com.szmsd.delivery.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "", description = "DelTimelinessConfig对象")
public class DelTimelinessConfig {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    @Excel(name = "主键ID")
    private Integer id;

    @ApiModelProperty(value = "时效开始时间")
    @Excel(name = "时效开始时间")
    private Date sectionBeginTime;

    @ApiModelProperty(value = "时效结束时间")
    @Excel(name = "时效结束时间")
    private Date sectionEndTime;

    @ApiModelProperty(value = "区间划分")
    @Excel(name = "区间划分")
    private Integer sectionSky;

    @ApiModelProperty(value = "区间排序")
    @Excel(name = "区间排序")
    private Integer sectionOrder;
}