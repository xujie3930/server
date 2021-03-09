package com.szmsd.http.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:10
 */
@Data
@ApiModel(value = "ShipmentCancelRequestDto", description = "ShipmentCancelRequestDto对象")
public class ShipmentCancelRequestDto implements Serializable {

    @ApiModelProperty(value = "仓库代码")
    private String warehouseCode;

    @ApiModelProperty(value = "订单号列表")
    private List<String> orderNoList;

    @ApiModelProperty(value = "备注")
    private String remark;
}
