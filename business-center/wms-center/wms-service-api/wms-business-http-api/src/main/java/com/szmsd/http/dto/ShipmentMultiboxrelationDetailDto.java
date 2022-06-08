package com.szmsd.http.dto;

import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhangyuyuan
 * @date 2021-03-09 11:16
 */
@Data
@ApiModel(value = "ShipmentMultiboxrelationRequestDto", description = "ShipmentMultiboxrelationRequestDto对象")
public class ShipmentMultiboxrelationDetailDto implements Serializable {

    @ApiModelProperty(value = "sku")
    private String sku;

    @ApiModelProperty(value = "箱号")
    private String boxNo;

    @ApiModelProperty(value = "跟踪号")
    private String trackingNo;

}
