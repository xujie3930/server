package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "DelOutbounderrorEmail", description = "DelOutbounderrorEmail")
public class DelOutbounderrorEmail {

    @ApiModelProperty(value = "异常描述WMS")
    private String exceptionMessageWms;

    @ApiModelProperty(value = "单号")
    private String orderNo;

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
