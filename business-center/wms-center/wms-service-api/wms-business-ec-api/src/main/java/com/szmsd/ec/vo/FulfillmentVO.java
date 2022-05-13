package com.szmsd.ec.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @FileName FulfillmentVo.java
 * @Description ----------功能描述---------
 * @Date 2021-04-20 17:14
 * @Author hyx
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class FulfillmentVO {
    /**
     * 电商订单号
     */
    @ApiModelProperty(value = "电商订单号")
    private String orderNo;
    /**
     * 推送至电商是否成功
     */
    @ApiModelProperty(value = "推送至电商是否成功")
    private boolean status;
    /**
     * 推送至电商失败原因
     */
    @ApiModelProperty(value = "推送至电商失败原因")
    private String desc;
}