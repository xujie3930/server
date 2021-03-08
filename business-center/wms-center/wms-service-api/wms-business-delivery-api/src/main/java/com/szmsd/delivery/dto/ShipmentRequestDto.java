package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-08 18:53
 */
@Data
@ApiModel(value = "ShipmentRequestDto", description = "ShipmentRequestDto对象")
public class ShipmentRequestDto implements Serializable {

    @ApiModelProperty(value = "操作人")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateOn;

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "出库单集合")
    private List<String> shipmentList;

    @ApiModelProperty(value = "操作类型，开始处理：Processing，已发货：Shipped，已取消：Canceled")
    private String operationType;

    @ApiModelProperty(value = "操作时间")
    private String operationTime;

    @ApiModelProperty(value = "备注")
    private String remark;
}
