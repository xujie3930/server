package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.szmsd.common.core.annotation.Excel;
import com.szmsd.common.core.utils.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DelOutboundBoxLabelDto {

    @ExcelProperty(index = 0)
    @ApiModelProperty(value = "页码")
    @Excel(name = "页码" ,type = Excel.Type.IMPORT)
    private String businessItemNo;

    @ExcelProperty(index = 1)
    @ApiModelProperty(value = "识别的箱标号")
    @Excel(name = "识别的箱标号" ,type = Excel.Type.IMPORT)
    private String businessNo;

    @ExcelProperty(index = 2)
    @ApiModelProperty(value = "出库单号")
    @Excel(name = "出库单号" ,type = Excel.Type.IMPORT)
    private String remark;


}
