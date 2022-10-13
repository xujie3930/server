package com.szmsd.delivery.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

@Data
@ApiModel(value = "DelOutboundBatchUpdateTrackingNoEmailDto", description = "DelOutboundBatchUpdateTrackingNoEmailDto")
public class DelOutboundBatchUpdateTrackingNoEmailDto {

    @Excel(name = "出库单号",width = 30)
    private String orderNo;

    @Excel(name = "挂号",width = 30)
    private String trackingNo;

    private String customCode;

    //员工编号
    private String empCode;

    //员工的邮箱
    private String email;
}