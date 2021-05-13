package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-25 14:42
 */
@Data
@ApiModel(value = "DelOutboundBringVerifyVO", description = "DelOutboundBringVerifyVO对象")
public class DelOutboundBringVerifyVO implements Serializable {

    @ApiModelProperty(value = "单号")
    private String orderNo;

    @ApiModelProperty(value = "true成功，false失败")
    private Boolean success;

    @ApiModelProperty(value = "消息")
    private String message;

    public DelOutboundBringVerifyVO() {
    }

    public DelOutboundBringVerifyVO(String orderNo, Boolean success, String message) {
        this.orderNo = orderNo;
        this.success = success;
        this.message = message;
    }
}
