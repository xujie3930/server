package com.szmsd.delivery.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.szmsd.common.core.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "导入模板")
public class DelQueryServiceImportExcle {
    private static final long serialVersionUID = 1L;

    @ExcelProperty(index = 0)
    @Excel(name = "订单号" ,width = 30, type = Excel.Type.ALL)
    private String orderNo;

    @ExcelProperty(index = 1)
    @Excel(name = "查件原因",width = 30, type = Excel.Type.ALL)
    private String reason;

    @ExcelProperty(index = 2)
    @Excel(name = "备注",width = 30, type = Excel.Type.ALL)
    private String remark;

    @ApiModelProperty(value = "操作类型(0表示管理端,1表示客户端)")
    @TableField(exist = false)
    private int operationType=0;
}
