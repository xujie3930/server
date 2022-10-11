package com.szmsd.delivery.dto;


import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "DelOutboundBatchUpdateTrackingNoEmailDto", description = "DelOutboundBatchUpdateTrackingNoEmailDto")
public class DelOutboundBatchUpdateTrackingNoEmailDto {

    private String orderNo;

    private String trackingNo;

    private String customCode;

    //员工编号
    private String empCode;
}
