package com.szmsd.chargerules.export;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.szmsd.http.dto.custom.AssociatedCustomersDto;
import com.szmsd.http.dto.grade.GradeDetailDto;
import com.szmsd.http.vo.DateOperation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author zhangyuyuan
 * @date 2021-03-05 14:21
 */
@Data
@ColumnWidth(15)
public class GradeExportListVO implements Serializable {

    @ExcelProperty(value = "方案名称")
    private String name;

    @ExcelProperty(value = "优先级")
    private String order;


    @ExcelProperty(value = "生效时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveBeginTime;

    @ExcelProperty(value = "截止时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date effectiveEndTime;

    @ExcelProperty(value = "备注")
    private String remark;


    @ExcelProperty(value = "操作人")
    private String creation;

    @ExcelProperty(value = "更新时间")
    private Date time;

}
