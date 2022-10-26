package com.szmsd.delivery.domain;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "挂号推送表", description = "BasTrackingPush对象")
public class BasTrackingPush {
    private Integer id;

    private String warehouseCode;

    private String orderNo;

    private String trackingNo;

    private String type;


}