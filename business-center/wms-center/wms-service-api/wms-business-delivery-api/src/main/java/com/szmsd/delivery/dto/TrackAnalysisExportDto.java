package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TrackAnalysisExportDto {

    @Excel(name = "提审时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String bringVerifyTime;

    @Excel(name = "订单号")
    private String orderNo;

    @Excel(name = "跟踪号")
    private String trackingNo;

    @Excel(name = "参考号")
    private String refNo;

    @Excel(name = "产品代码")
    private String shipmentRule;

    @Excel(name = "产品名称")
    private String shipmentRuleName;

    @Excel(name = "轨迹状态")
    private String trackingStatus;

    @Excel(name = "最新轨迹时间")
    private String latestTrackTime;

    @Excel(name = "最新轨迹信息")
    private String latestTrackInfo;

}
