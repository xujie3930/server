package com.szmsd.delivery.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

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
    //客服
    private String serviceStaffName;

    //客服经理
    private String serviceManagerName;

    //原单号
    private String noTrackingNo;

    //客服邮箱

    private String sellerEmail;



}
