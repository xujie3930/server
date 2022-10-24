package com.szmsd.delivery.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="查件服务异常导出对象", description="DelQueryServiceExcErroe对象")
public class DelQueryServiceExcErroe {



    @ApiModelProperty(value = "订单号/跟踪号")
    @Excel(name="订单号/跟踪号",width = 30,needMerge = true)
    private String orderNoTraceid;

    @ApiModelProperty(value = "反馈类容")
    @Excel(name="反馈类容",width = 30,needMerge = true)
    private String feedReason;

    @ApiModelProperty(value = "失败类容")
    @Excel(name="失败类容",width = 30,needMerge = true)
    private String errorMessage;





}
