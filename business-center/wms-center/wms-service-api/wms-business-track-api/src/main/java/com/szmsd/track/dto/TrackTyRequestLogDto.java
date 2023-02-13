package com.szmsd.track.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class TrackTyRequestLogDto implements Serializable {

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "接口类型")
    private String type;
}
