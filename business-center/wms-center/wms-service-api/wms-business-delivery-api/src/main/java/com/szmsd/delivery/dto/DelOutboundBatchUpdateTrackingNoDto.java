package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ApiModel(value = "DelOutboundBatchUpdateTrackingNoDto", description = "DelOutboundBatchUpdateTrackingNoDto对象")
public class DelOutboundBatchUpdateTrackingNoDto implements Serializable {

    private String orderNo;

    private String trackingNo;

    private String amazonLogisticsRouteId;

    @ApiModelProperty("是否邮箱发送")
    private String emailType;

    private String noTrackingNo;

    private String customCode;

    private String refNo;



}
