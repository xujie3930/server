package com.szmsd.putinstorage.domain.dto;

import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @ClassName: InventoryStockByRangeDTO
 * @Description: sku入库状况查询条件
 * @Author: 11
 * @Date: 2021-10-21 18:27
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "sku入库状况查询条件")
public class InventoryStockByRangeDTO {
    @NotNull(message = "开始时间不能为空")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "开始时间 yyyy-MM-dd HH:mm:ss",example = "2021-10-01 10:10:10", required = true)
    private Date timeStart;
    @NotNull(message = "结束时间不能为空")
    @DateTimeFormat(value = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(value = "结束时间 yyyy-MM-dd HH:mm:ss",example = "2021-10-21 10:10:10", required = true)
    private Date timeEnd;

    public void valid() {
        if (timeEnd.before(timeStart)) throw new RuntimeException("结束时间不能在开始时间前");
    }
}
