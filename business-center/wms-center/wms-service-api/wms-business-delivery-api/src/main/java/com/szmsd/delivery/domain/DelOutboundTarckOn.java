package com.szmsd.delivery.domain;

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
@ApiModel(value = "出库单挂号记录表", description = "DelOutboundTarckOn对象")
public class DelOutboundTarckOn extends QueryDto {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "原挂号")
    private String trackingNo;

    @ApiModelProperty(value = "新挂号")
    private String trackingNoNew;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "单号")
    @TableField(exist = false)
    private  String oddNumbers;

    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    private String startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private String endTime;

    @ApiModelProperty(value = "orderNos")
    @TableField(exist = false)
    private String orderNos;

    @ApiModelProperty(value = "orderNosList")
    @TableField(exist = false)
    private List<String> orderNosList;

    @ApiModelProperty(value = "ids")
    @TableField(exist = false)
    private List<String> ids;



}