package com.szmsd.track.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TrackAnalysisRequestDto {

    @ApiModelProperty("时间类型 1创建时间  2发货时间 3.最新轨迹时间")
    private Integer dateType;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;
//
//    @ApiModelProperty("轨迹开始时间")
//    private String trackingStartTime;
//
//    @ApiModelProperty("轨迹结束时间")
//    private String trackingEndTime;

    @ApiModelProperty(value = "发货服务")
    private String shipmentService;

    @ApiModelProperty(value = "国家代码")
    private String countryCode;

    @ApiModelProperty(value = "国家名称")
    private String countryName;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "状态")
    private String trackingStatus;

    @ApiModelProperty(value = "系统语言")
    private String lang;
}
