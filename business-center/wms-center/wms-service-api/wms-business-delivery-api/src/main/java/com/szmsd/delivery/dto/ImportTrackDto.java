package com.szmsd.delivery.dto;

import com.szmsd.bas.plugin.BasSubCommonPlugin;
import com.szmsd.bas.plugin.BasSubValueCommonParameter;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.plugin.annotation.AutoFieldValue;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ImportTrackDto {



//    @ApiModelProperty(value = "运输商编码")
//    @Excel(name = "运输商编码")
//    private String carrierCode;

    @ApiModelProperty(value = "订单号")
    @Excel(name = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "物流跟踪号")
    @Excel(name = "物流跟踪号")
    private String trackingNo;

    @AutoFieldValue(supports = BasSubCommonPlugin.SUPPORTS, code = "099", cp = BasSubValueCommonParameter.class)
    @ApiModelProperty(value = "轨迹状态")
    private String trackingStatus;

    @ApiModelProperty(value = "轨迹信息描述")
    @Excel(name = "轨迹信息描述")
    private String description;
}
