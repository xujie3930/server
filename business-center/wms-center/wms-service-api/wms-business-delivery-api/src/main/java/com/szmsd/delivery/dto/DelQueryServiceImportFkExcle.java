package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "导入模板")
public class DelQueryServiceImportFkExcle {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0)
    @Excel(name = "订单号/跟踪号" ,width = 30, type = Excel.Type.ALL)
    private String orderNoTraceid;


    @ExcelProperty(index = 1)
    private String feedReason;
}
