package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-08-04 9:22
 */
@Data
@ApiModel(value = "DelOutboundLabelResponse", description = "DelOutboundLabelResponse对象")
public class DelOutboundLabelResponse implements Serializable {

    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "文件名称")
    private String fileName;

    @ApiModelProperty(value = "文件Base64")
    private String base64;

    @ApiModelProperty(value = "状态")
    private Boolean status;

    @ApiModelProperty(value = "消息")
    private String message;

}
