package com.szmsd.delivery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "DelOutboundReportListVO", description = "DelOutboundReportListVO对象")
public class DelOutboundReportListVO implements Serializable {

    @ApiModelProperty(value = "时间")
    private String date;

    @ApiModelProperty(value = "数量")
    private Integer count;

    public DelOutboundReportListVO() {
    }

    public DelOutboundReportListVO(String date, Integer count) {
        this.date = date;
        this.count = count;
    }
}
