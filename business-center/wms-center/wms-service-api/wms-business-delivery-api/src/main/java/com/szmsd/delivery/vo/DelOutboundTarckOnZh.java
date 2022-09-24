package com.szmsd.delivery.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.szmsd.common.core.web.controller.QueryDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
@ApiModel(value = "出库单挂号记录中文", description = "DelOutboundTarckOn对象")
public class DelOutboundTarckOnZh  {


    @ApiModelProperty(value = "出库单号")
    @Excel(name = "出库单号",width = 30)
    private String orderNo;

    @ApiModelProperty(value = "原挂号")
    @Excel(name = "原挂号",width = 30)
    private String trackingNo;

    @ApiModelProperty(value = "新挂号")
    @Excel(name = "新挂号",width = 30)
    private String trackingNoNew;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新时间")
    @Excel(name = "更新时间",width = 30)
    private String updateTimes;




}