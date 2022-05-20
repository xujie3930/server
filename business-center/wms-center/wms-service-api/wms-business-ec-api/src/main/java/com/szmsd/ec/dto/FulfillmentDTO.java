package com.szmsd.ec.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @FileName FulfillmentDto.java
 * @Description ----------功能描述---------
 * @Date 2021-04-16 16:08
 * @Author hyx
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
public class FulfillmentDTO {
    /**
     * 电商订单号
     */
    @ApiModelProperty(value = "电商订单号")
    private String orderNo;
    /**
     * 电商名称-数据字段对应
     */
    @ApiModelProperty(value = "电商名称-数据字段对应")
    private String eCommerceName;
    /**
     * 订单明细
     */
    @ApiModelProperty(value = "订单明细")
    private List<FulfillmentDetailDTO> fulfillmentDetailDtos;
}