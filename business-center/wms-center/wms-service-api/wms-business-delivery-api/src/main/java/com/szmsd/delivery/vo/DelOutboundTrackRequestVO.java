package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DelOutboundTrackRequestVO implements Serializable {

    @ApiModelProperty(value = "出库单ID")
    private Long delOutboundId;

    @ApiModelProperty(value = "出库单号")
    private String orderNo;

    @ApiModelProperty(value = "轨迹状态")
    private String trackingStatus;

    @ApiModelProperty(value = "轨迹信息描述")
    private String trackingDescription;

    @ApiModelProperty(value = "最新轨迹时间")
    private Date trackingTime;

    @ApiModelProperty(value = "妥投时间")
    private Date deliveredDime;

    @ApiModelProperty(value = "时间差")
    private Integer timeDifference;

    private String tyShipmentId;



}
