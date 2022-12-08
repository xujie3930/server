package com.szmsd.delivery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DelOutboundsuccessEmail", description = "DelOutboundsuccessEmail")
public class DelOutboundsuccessEmail {

    @ApiModelProperty(value = "异常描述WMS")
    private String exceptionMessageWms;

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "客户代码")
    private String customCode;

    @ApiModelProperty(value = "出库订单类型")
    private String orderType;

    @ApiModelProperty(value = "物流服务")
    private String prcInterfaceProductCode;

    @ApiModelProperty(value = "国家")
    private String country;


    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String createTime;

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
