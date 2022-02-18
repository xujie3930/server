package com.szmsd.returnex.dto;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.JSONObject;
import com.szmsd.common.core.validator.annotation.StringLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName: ReturnExpressAddDTO
 * @Description: 新增退货单处理
 * @Author: 11
 * @Date: 2021/3/26 16:45
 */
@Data
@EqualsAndHashCode
@ApiModel("客户端导入处理方式对象")
public class ReturnExpressClientImportBaseDTO {

    @NotBlank(message = "出库单号不能为空")
    @ExcelProperty(value = "预报单号", index = 0)
    @ApiModelProperty(value = "预报单号 系统生成", required = true)
    private String expectedNo;

    @ExcelIgnore
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "068002", required = true)
    private String processType;

    @ExcelProperty(value = "申请处理方式", index = 1)
    @ApiModelProperty(value = "申请处理方式 ", allowableValues = "-", notes = " 销毁 包裹上架 拆包检查", example = "销毁", required = true)
    private String processTypeStr;

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
