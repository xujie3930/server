package com.szmsd.ec.dto;

import com.szmsd.ec.enums.OrderStatusEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TransferCallbackDTO {

    @ApiModelProperty(value = "电商单号")
    @NotBlank(message = "电商单号不能为空")
    private String orderNo;

    @ApiModelProperty(value = "运单号")
    private String waybillNo;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;

    @ApiModelProperty(value = "仓库名称")
    private String warehouseName;

    /**
     * 订单状态
     */
    @NotNull(message = "订单状态不能为空")
    private OrderStatusEnum status;

    /**
     * 转仓库单异常信息
     */
    private String transferErrorMsg;

    @ApiModelProperty(value = "订单类型  （小包集货/小包备货）")
    private String orderType;

}
