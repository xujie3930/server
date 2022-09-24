package com.szmsd.delivery.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value = "出库单挂号记录表英文", description = "DelOutboundTarckOnEh对象")
public class DelOutboundTarckOnEh {


    @ApiModelProperty(value = "出库单号")
    @Excel(name = "orderNo",width = 30)
    private String orderNo;

    @ApiModelProperty(value = "原挂号")
    @Excel(name = "trackingNo",width = 30)
    private String trackingNo;

    @ApiModelProperty(value = "新挂号")
    @Excel(name = "trackingNoNew",width = 30)
    private String trackingNoNew;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "更新时间")
    @Excel(name = "updateTimes",width = 30)
    private String updateTimes;




}