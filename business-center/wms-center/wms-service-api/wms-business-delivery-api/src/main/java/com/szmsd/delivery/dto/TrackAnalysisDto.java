package com.szmsd.delivery.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 轨迹分析
 */
@Data
public class TrackAnalysisDto {

    @ApiModelProperty("key中文")
    private String keyName;

    @ApiModelProperty("key code")
    private String keyCode;

    @ApiModelProperty("统计数量")
    private Integer num;
}
