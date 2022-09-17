package com.szmsd.delivery.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class DelQueryServiceFeedbackExc {



    @ApiModelProperty(value = "反馈内容")
    @Excel(name="反馈内容",width = 30,needMerge = true)
    private String reason;

    @ApiModelProperty(value = "反馈时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name="反馈时间",width = 30,needMerge = true)
    private String createTime;

    @ApiModelProperty(value = "操作类型")
    @Excel(name = "状态名称")
    private String type;
}
