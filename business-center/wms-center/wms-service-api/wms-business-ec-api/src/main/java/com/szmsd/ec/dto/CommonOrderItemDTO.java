package com.szmsd.ec.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;


/**
* <p>
    * 电商平台公共订单明细表
    * </p>
*
* @author zengfanlang
* @since 2021-12-17
*/
@Data
@Accessors(chain = true)
@ApiModel(value="电商平台公共订单明细表", description="EcCommonOrderItem对象")
@TableName("ec_common_order_item")
public class CommonOrderItemDTO {

    private static final long serialVersionUID = 1L;

    @Excel(name = "")
    private Long id;

    @Excel(name = "")
    private String itemId;

    @ApiModelProperty(value = "订单表id")
    @Excel(name = "订单表id")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "商品描述")
    @Excel(name = "商品描述")
    private String title;

    @ApiModelProperty(value = "ASIN")
    @Excel(name = "ASIN")
    private String asin;

    @ApiModelProperty(value = "SKU")
    @Excel(name = "SKU")
    private String platformSku;

    @ApiModelProperty(value = "数量")
    @Excel(name = "数量")
    private Integer quantity;

    @ApiModelProperty(value = "单价")
    @Excel(name = "单价")
    private BigDecimal price;

    @ApiModelProperty(value = "总价值")
    @Excel(name = "总价值")
    private BigDecimal declareValue;

}
