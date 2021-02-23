package com.szmsd.ord.api.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author lufei
 * @version 1.0
 * @Date 2020-06-10 14:13
 * @Description
 */
@Data
public class OrderDto extends OrderVo {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "起始下单日期")
    private String orderDateStart;

    @ApiModelProperty(value = "截止下单日期")
    private String orderDateEnd;

    @ApiModelProperty(value = "起始录单日期")
    private String createTimeStart;

    @ApiModelProperty(value = "截止录单日期")
    private String createTimeEnd;

    @ApiModelProperty(value = "订单状态")
    private String orderStatus;

    @ApiModelProperty(value = "批量查询订单号")
    private String orderNosStr;

    @ApiModelProperty(value = "批量查询运单号")
    private String waybillNosStr;

    @ApiModelProperty(value = "订单号集合")
    private List<String> orderNos;

    @ApiModelProperty(value = "运单号集合")
    private List<String> waybillNos;

    @ApiModelProperty(value = "订单状态集合")
    private List<String> orderStatusList;

    @ApiModelProperty(value = "清关费集合")
    private Map<String, BigDecimal> map;

}
